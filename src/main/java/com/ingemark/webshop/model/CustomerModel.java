package com.ingemark.webshop.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "webshop_customer")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerModel {
    @Id
    private Integer id;
    private String first_name;
    private String last_name;
    private String email;
}
