package com.pishaped.product.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/products")
public class ProductController {
    @Value("${some.dynamic.property:Default Value}")
    private String dynamicMessage;

    @GetMapping("/message")
    public String getMessage() {
        return "Product Service Message: " + dynamicMessage;
    }

    @GetMapping
    public String getProducts() {
        return "Products from Product Service";
    }

}
