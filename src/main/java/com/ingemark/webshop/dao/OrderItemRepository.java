package com.ingemark.webshop.dao;

import com.ingemark.webshop.model.OrderItemModel;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItemModel, Long> {
}
