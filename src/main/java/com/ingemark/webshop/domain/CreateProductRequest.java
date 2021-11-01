package com.ingemark.webshop.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

public class CreateProductRequest {
    @Pattern(regexp = ".{10}")
    public String code;
    public String name;
    public String description;
    @Min(0)
    public double price_hrk;
    public boolean is_available;
}
