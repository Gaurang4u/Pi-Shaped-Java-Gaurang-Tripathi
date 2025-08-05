package com.pishaped.api.gateway.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.ok("User Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/fallback/order")
    public ResponseEntity<String> orderFallback() {
        return ResponseEntity.ok("Order Service is temporarily unavailable. Please try again later.");
    }
}
