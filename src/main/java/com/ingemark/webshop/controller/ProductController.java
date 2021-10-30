package com.ingemark.webshop.controller;

import com.ingemark.webshop.ProductRepository;
import com.ingemark.webshop.model.ProductModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@Service
@AllArgsConstructor
public class ProductController {

    public final ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public @ResponseBody
    Optional<ProductModel> getProduct(@PathVariable Integer id) {
        //TODO: return 404 if not found
        return productRepository.findById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Integer createProduct(HttpServletRequest request, Model model) {
        ProductModel product = new ProductModel();
        product.setId(1);
        product.setCode("1234567890");
        product.setDescription("Really good");
        product.setName("Hair gel");
        product.setIs_available(true);

        productRepository.save(product);
        return 3;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public @ResponseBody
    Integer deleteProduct(@PathVariable Integer id) {
        productRepository.deleteById(id);
        //TODO: return 404 if not found
        return null;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public @ResponseBody
    Integer updateProduct(@PathVariable Integer id) {
        //TODO: return 404 if not found
        return 3;
    }
}
