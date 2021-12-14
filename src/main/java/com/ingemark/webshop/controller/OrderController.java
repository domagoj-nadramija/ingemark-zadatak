package com.ingemark.webshop.controller;

import com.ingemark.webshop.dao.OrderItemRepository;
import com.ingemark.webshop.dao.OrderRepository;
import com.ingemark.webshop.domain.CreateOrderRequest;
import com.ingemark.webshop.model.OrderItemModel;
import com.ingemark.webshop.model.OrderModel;
import com.ingemark.webshop.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@Service
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final JdbcTemplate jdbcTemplate;

    private OrderModel getOrderOr404(Long id) {
        Optional<OrderModel> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found!");
        } else {
            return order.get();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    JSONObject getAllOrders() {
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Orders successfully retrieved");
        response.put("items", orderRepository.findAll());
        return new JSONObject(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public @ResponseBody
    JSONObject getOrder(@PathVariable Long id) {
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Order successfully retrieved");
        response.put("item", getOrderOr404(id));
        return new JSONObject(response);
    }

    @RequestMapping(method = RequestMethod.POST)
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
        ArrayList<OrderItemModel> orderItems = new ArrayList<>();

        Long orderId = order.getId();
        for (HashMap<String, Long> item : items) {
            OrderItemModel newOrderItem = new OrderItemModel();
            Long productId = item.get("product_id");
            Long quantity = item.get("quantity");
            newOrderItem.setOrder_id(orderId);
            newOrderItem.setProduct_id(productId);
            newOrderItem.setQuantity(quantity);
            orderItems.add(newOrderItem);
        }
        try {
            orderItemRepository.saveAll(orderItems);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid product ID!");
        }

        String totalPriceQuery = "select sum(price_hrk*quantity) from webshop_order_item join webshop_product on webshop_order_item.product_id = webshop_product.id where webshop_order_item.order_id = 10 group by webshop_order_item.order_id;";
        Double totalPrice = jdbcTemplate.queryForObject(totalPriceQuery, Double.class);
        order.setPrice_hrk(totalPrice);
        orderRepository.save(order);

        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Order successfully created");
        response.put("new_item_id", order.getId());
        return new JSONObject(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public @ResponseBody
    JSONObject deleteOrder(@PathVariable Long id) {
        OrderModel order = getOrderOr404(id);
        orderRepository.deleteById(id);
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Order successfully deleted");
        response.put("deleted_item", order);
        return new JSONObject(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    JSONObject updateOrder(@PathVariable Long id, @RequestBody String body) {
        OrderModel order = getOrderOr404(id);

        //TODO

        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Order successfully updated");

        return new JSONObject(response);
    }

}
