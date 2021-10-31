package com.ingemark.webshop.controller;

import com.ingemark.webshop.ProductRepository;
import com.ingemark.webshop.domain.CreateProductRequest;
import com.ingemark.webshop.model.ProductModel;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/products")
@Service
@AllArgsConstructor
public class ProductController {

    public final ProductRepository productRepository;

    private ProductModel getProductOr404(Long id) {
        Optional<ProductModel> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!");
        } else {
            return product.get();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Iterable<ProductModel> getAllProducts() {
        return productRepository.findAll();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public @ResponseBody
    ProductModel getProduct(@PathVariable Long id) {
        return getProductOr404(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Long createProduct(@RequestBody CreateProductRequest body) {
        ProductModel product = new ProductModel();
        product.setCode(body.code);
        product.setDescription(body.description);
        product.setName(body.name);
        product.setPrice_hrk(body.price_hrk);
        product.setIs_available(body.is_available);
        try {
            product = productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code!");
        }

        return product.getId();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public @ResponseBody
    ProductModel deleteProduct(@PathVariable Long id) {
        ProductModel product = getProductOr404(id);
        productRepository.deleteById(id);
        return product;
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}")
    public @ResponseBody
    Long updateProduct(@PathVariable Long id) {
        getProductOr404(id);
        //TODO
        return 3L;
    }
}
