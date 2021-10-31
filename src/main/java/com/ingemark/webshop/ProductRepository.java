package com.ingemark.webshop;

import com.ingemark.webshop.model.ProductModel;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductModel, Long> {
}
