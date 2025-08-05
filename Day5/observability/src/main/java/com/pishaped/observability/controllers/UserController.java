package com.pishaped.observability.controllers;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final MeterRegistry meterRegistry;

    public UserController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable String id) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
        long duration = System.currentTimeMillis() - start;

        meterRegistry.timer("user.api.latency").record(duration, TimeUnit.MILLISECONDS);

        return ResponseEntity.ok("User " + id);
    }
}
