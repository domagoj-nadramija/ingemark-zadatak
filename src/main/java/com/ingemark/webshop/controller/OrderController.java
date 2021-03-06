package com.ingemark.webshop.controller;

import com.ingemark.webshop.dao.OrderItemRepository;
import com.ingemark.webshop.dao.OrderRepository;
import com.ingemark.webshop.dao.ProductRepository;
import com.ingemark.webshop.domain.CreateOrderRequest;
import com.ingemark.webshop.model.OrderItemModel;
import com.ingemark.webshop.model.OrderModel;
import com.ingemark.webshop.model.OrderStatus;
import com.ingemark.webshop.model.ProductModel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

@Controller
@RequestMapping("/orders")
@Service
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private OrderModel getOrderOr404(Long id) {
        Optional<OrderModel> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found!");
        } else {
            return order.get();
        }
    }

    private JSONObject jsonResponse(String message, Map<String, Object> otherFields) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.putAll(otherFields);
        return new JSONObject(response);
    }

    private Double getExchangeRateHrkEur() throws ParseException, java.text.ParseException {
        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get().uri("https://api.hnb.hr/tecajn/v2?valuta=EUR").retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();

        JSONArray parsedBody = (JSONArray) new JSONParser().parse(responseBody);
        // API returns exchange rate as string with comma instead of period
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(symbols);
        return (Double) df.parse((String) ((JSONObject) parsedBody.get(0)).get("srednji_tecaj"));
    }

    private void addItemsToOrder(Long orderId, ArrayList<HashMap<String, Long>> items) {
        ArrayList<OrderItemModel> orderItems = new ArrayList<>();
        Set<Long> productIds = new LinkedHashSet<>();
        for (HashMap<String, Long> item : items) {
            OrderItemModel newOrderItem = new OrderItemModel();
            Long productId = item.get("product_id");
            productIds.add(productId);
            Long quantity = item.get("quantity");
            newOrderItem.setOrder_id(orderId);
            newOrderItem.setProduct_id(productId);
            newOrderItem.setQuantity(quantity);
            orderItems.add(newOrderItem);
        }
        ArrayList<ProductModel> foundProducts = (ArrayList<ProductModel>) productRepository.findAllById(productIds);
        // check that we found all products
        if (foundProducts.size() < productIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown product ID!");
        }
        // check that all products to be added to order are "available"
        for (ProductModel product : foundProducts) {
            if (! product.getIs_available()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add unavailable product to order!");
            }
        }
        // remove old items from order
        String totalPriceQuery = "delete from webshop_order_item where order_id = :id;";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", orderId);
        namedParameterJdbcTemplate.update(totalPriceQuery, namedParameters);

        try {
            orderItemRepository.saveAll(orderItems);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more items in order are invalid!");
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    JSONObject getAllOrders() {
        return jsonResponse("Orders successfully retrieved", Map.of("items", orderRepository.findAll()));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public @ResponseBody
    JSONObject getOrder(@PathVariable Long id) {
        String getOrderItemsQuery = "select webshop_order_item.quantity, webshop_product.code, webshop_product.\"name\", webshop_product.price_hrk, webshop_product.description, webshop_product.is_available from webshop_order_item join webshop_product on webshop_order_item.product_id = webshop_product.id where webshop_order_item.order_id =  :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        var orderItems  = namedParameterJdbcTemplate.queryForList(getOrderItemsQuery, namedParameters);
        return jsonResponse("Order successfully retrieved", Map.of("order", getOrderOr404(id), "order_items", orderItems));
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public @ResponseBody
    JSONObject createOrder(@Valid @RequestBody CreateOrderRequest body) {
        OrderModel order = new OrderModel();
        order.setCustomer_id(body.customer_id);
        order.setStatus(OrderStatus.DRAFT);
        try {
            orderRepository.save(order);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid customer ID!");
        }

        ArrayList<HashMap<String, Long>> items = body.items;

        Long orderId = order.getId();
        addItemsToOrder(orderId, items);

        return jsonResponse("Order successfully created", Map.of("new_item_id", order.getId()));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    JSONObject deleteOrder(@PathVariable Long id) {
        OrderModel order = getOrderOr404(id);
        orderRepository.deleteById(id);
        return jsonResponse("Order successfully deleted", Map.of("deleted_item", order));
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public @ResponseBody
    JSONObject updateOrder(@PathVariable Long id, @RequestBody String body) {
        OrderModel order = getOrderOr404(id);
        HashMap<String, Object> oldOrderHashMap = order.toHashMap();
        Long orderId = order.getId();
        JSONObject parsedBody;
        try {
            parsedBody = (JSONObject) new JSONParser().parse(body);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON body!");
        }
        parsedBody.forEach((key, value) -> {
            switch ((String) key) {
                case "customer_id" -> order.setCustomer_id((Long) value);
                case "items" -> addItemsToOrder(orderId,(JSONArray) value);
            }
        });
        try {
            orderRepository.save(order);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid customer ID!");
        }
        return jsonResponse("Order successfully updated", Map.of("old_item", oldOrderHashMap, "updated_item", order.toHashMap()));
    }

    @SneakyThrows
    @RequestMapping(method = RequestMethod.POST, value = "/finalize/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    JSONObject finalizeOrder(@PathVariable Long id) {
        OrderModel order = getOrderOr404(id);
        Long orderId = order.getId();
        String totalPriceQuery = "select sum(price_hrk*quantity) from webshop_order_item join webshop_product on webshop_order_item.product_id = webshop_product.id where webshop_order_item.order_id = :id group by webshop_order_item.order_id;";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", orderId);
        Double totalPriceHrk = namedParameterJdbcTemplate.queryForObject(totalPriceQuery, namedParameters, Double.class);
        order.setPrice_hrk(totalPriceHrk);
        Double exchangeRateHrkEur = getExchangeRateHrkEur();
        order.setPrice_eur(new BigDecimal(totalPriceHrk / exchangeRateHrkEur).setScale(2, RoundingMode.HALF_UP).doubleValue());
        order.setStatus(OrderStatus.SUBMITTED);
        orderRepository.save(order);
        return jsonResponse("Order successfully finalized", Map.of("item", order));
    }
}
