package com.ingemark.webshop.controller;

import com.ingemark.webshop.ProductRepository;
import com.ingemark.webshop.domain.CreateProductRequest;
import com.ingemark.webshop.model.ProductModel;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@Service
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

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
    JSONObject getAllProducts() {
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Products successfully retrieved");
        response.put("items", productRepository.findAll());
        return new JSONObject(response);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public @ResponseBody
    JSONObject getProduct(@PathVariable Long id) {
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Product successfully retrieved");
        response.put("item", getProductOr404(id));
        return new JSONObject(response);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    JSONObject createProduct(@Valid @RequestBody CreateProductRequest body) {
        ProductModel product = new ProductModel();
        product.setCode(body.code);
        product.setDescription(body.description);
        product.setName(body.name);
        product.setPrice_hrk(body.price_hrk);
        product.setIs_available(body.is_available);
        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or duplicate code!");
        }
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Product successfully created");
        response.put("new_item_id", product.getId());
        return new JSONObject(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public @ResponseBody
    JSONObject deleteProduct(@PathVariable Long id) {
        ProductModel product = getProductOr404(id);
        productRepository.deleteById(id);
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Product successfully deleted");
        response.put("deleted_item", product);
        return new JSONObject(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    JSONObject updateProduct(@PathVariable Long id, @RequestBody String body) {
        ProductModel product = getProductOr404(id);
        HashMap<String,Object> oldProductHashMap = product.toHashMap();
        JSONObject parsedBody;
        try {
            parsedBody = (JSONObject) new JSONParser().parse(body);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON body!");
        }

        parsedBody.forEach( (key, value) -> {
            switch ((String) key) {
                case "code" -> product.setCode((String) value);
                case "name" -> product.setName((String) value);
                case "description" -> product.setDescription((String) value);
                case "price_hrk" -> product.setPrice_hrk((Double) value);
                case "is_available" -> product.setIs_available((Boolean) value);
            }
        });
        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or duplicate code!");
        }
        HashMap<String,Object> response = new HashMap<>();
        response.put("message", "Product successfully updated");
        response.put("old_item", oldProductHashMap);
        response.put("updated_item", product.toHashMap());
        return new JSONObject(response);
    }
}
