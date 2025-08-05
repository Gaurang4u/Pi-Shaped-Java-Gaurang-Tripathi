package com.pishaped.circuit.breaker.services;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class PaymentService {

    private final ExecutorService executor;

    public PaymentService(ExecutorService executor) {
        this.executor = executor;
    }

    @TimeLimiter(name = "paymentServiceTimeLimiter", fallbackMethod = "paymentFallback")
    @Retry(name = "paymentServiceRetry", fallbackMethod = "paymentFallback")
    @Bulkhead(name = "paymentServiceBulkhead", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "paymentFallback")
    public CompletableFuture<String> processPayment(boolean fail, boolean delay) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("🔧 Processing payment: fail={}, delay={}", fail, delay);

            if (fail) {
                throw new RuntimeException("Simulated failure");
            }

            if (delay) {
                try {
                    Thread.sleep(3000); // Simulates long-running task
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            return "✅ Payment processed successfully!";
        }, executor); // ✅ Use injected executor
    }

    private CompletableFuture<String> paymentFallback(boolean fail, boolean delay, Throwable t) {
        log.warn("🔥 Fallback triggered: {}", t.toString());
        return CompletableFuture.completedFuture("❌ Payment failed: " + t.getClass().getSimpleName() + " - " + t.getMessage());
    }
}
