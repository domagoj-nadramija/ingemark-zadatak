package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "webshop_order_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemModel {
    @Id
    private Integer id;
    private Integer order_id;
    private Integer product_id;
    private Integer quantity;
}
