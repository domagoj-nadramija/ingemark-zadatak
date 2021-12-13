package com.ingemark.webshop.dao;

import com.ingemark.webshop.model.OrderModel;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderModel, Long> {
}
