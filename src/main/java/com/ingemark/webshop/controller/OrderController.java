package com.ingemark.webshop.controller;

import com.ingemark.webshop.dao.OrderRepository;
import com.ingemark.webshop.domain.CreateOrderRequest;
import com.ingemark.webshop.model.OrderModel;
import com.ingemark.webshop.model.OrderStatus;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@Service
@AllArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

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

        // create orderItems from request

        // if orderItems fail creation delete created orderItems and then order

        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Order successfully created");
        response.put("new_item_id", order.getId());
        return new JSONObject(response);
    }

}
