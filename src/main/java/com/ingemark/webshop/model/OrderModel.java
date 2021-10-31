package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "webshop_order")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderModel {
    @Id
    private Long id;
    private Long customer_id;
    private String status;
    private Double price_hrk;
    private Double price_eur;
}
