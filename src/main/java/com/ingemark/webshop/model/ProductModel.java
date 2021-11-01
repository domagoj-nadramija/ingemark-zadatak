package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.HashMap;

@Entity
@Table(name = "webshop_product")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @Pattern(regexp = ".{10}")
    private String code;
    private String name;
    @Min(0)
    private Double price_hrk;
    private String description;
    private Boolean is_available;

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("code", code);
        hashMap.put("name", name);
        hashMap.put("price_hrk", price_hrk);
        hashMap.put("description", description);
        hashMap.put("is_available", is_available);
        return hashMap;
    }
}
