package com.pishaped.cloud.gateway.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class GatewayConfig {
    @Value("${some.dynamic.property}")
    private String property;

    public String getProperty() {
        return property;
    }
}
