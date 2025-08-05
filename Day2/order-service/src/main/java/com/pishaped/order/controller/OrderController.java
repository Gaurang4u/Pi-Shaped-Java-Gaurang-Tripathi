package com.pishaped.order.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Value("${some.dynamic.property:Default Value}")
    private String dynamicMessage;

    @GetMapping("/message")
    public String getMessage() {
        return "Order Service Message: " + dynamicMessage;
    }

    @GetMapping
    public String getOrders() {
        return "Orders from Order Service";
    }
}
