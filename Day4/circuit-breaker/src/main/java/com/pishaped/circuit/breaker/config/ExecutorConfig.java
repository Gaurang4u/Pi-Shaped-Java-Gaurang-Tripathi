package com.pishaped.circuit.breaker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Bean
    public ExecutorService paymentExecutor() {
        return Executors.newFixedThreadPool(5); // Matches your Bulkhead thread pool size
    }
}
