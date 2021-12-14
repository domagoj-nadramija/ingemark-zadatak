package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashMap;

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

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("customer_id", customer_id);
        hashMap.put("status", status);
        hashMap.put("price_hrk", price_hrk);
        hashMap.put("price_eur", price_eur);
        return hashMap;
    }
}

