package com.pishaped.cloud.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @GetMapping("/admin/info")
    public String adminInfo() {
        return "This is secured admin info!";
    }
}
