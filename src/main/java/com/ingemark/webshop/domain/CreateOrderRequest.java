package com.ingemark.webshop.domain;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateOrderRequest {
    public Long customer_id;
    public ArrayList<HashMap<String,Long>> items;
}
