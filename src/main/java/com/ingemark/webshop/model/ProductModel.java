package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "webshop_product", schema = "webshop")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;
    @Column(unique = true)
    @Pattern(regexp = ".{10}")
    private String code;
    private String name;
    @Min(0)
    private Double price_hrk;
    private String description;
    private Boolean is_available;
}
