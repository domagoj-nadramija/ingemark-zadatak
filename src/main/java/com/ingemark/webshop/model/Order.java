package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "webshop_order")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private Integer id;
    private Integer customer_id;
    private String status;
    private Double price_hrk;
    private Double price_eur;
}
