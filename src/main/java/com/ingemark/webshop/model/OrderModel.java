package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "webshop_order")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customer_id;
    @Enumerated(EnumType.STRING)
    @Type(type = "com.ingemark.webshop.model.EnumTypePostgreSql")
    private OrderStatus status;
    private Double price_hrk;
    private Double price_eur;
}

