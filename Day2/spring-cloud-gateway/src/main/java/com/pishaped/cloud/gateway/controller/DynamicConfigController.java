package com.pishaped.cloud.gateway.controller;

import com.pishaped.cloud.gateway.config.GatewayConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynamicConfigController {
    private final GatewayConfig gatewayConfig;

    public DynamicConfigController(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }

    @GetMapping("/config/property")
    public String getProperty() {
        return "Current Property Value: " + gatewayConfig.getProperty();
    }
}
