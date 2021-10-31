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
    private Long id;
    private Long order_id;
    private Long product_id;
    private Integer quantity;
}
