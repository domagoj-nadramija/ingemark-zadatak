package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "webshop_product")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Integer id;
    private String code;
    private String name;
    private Double price_hrk;
    private String description;
    private Boolean is_available;
}
