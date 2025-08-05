package com.code.spring.taskmanagement.filter;

import com.code.spring.taskmanagement.config.RateLimiterProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.function.Supplier;

public class RateLimitFilter extends OncePerRequestFilter {

    private final LettuceBasedProxyManager<String> proxyManager;
    private final RateLimiterProperties properties;

    public RateLimitFilter(StatefulRedisConnection<String, byte[]> connection, RateLimiterProperties properties) {
        this.proxyManager = LettuceBasedProxyManager.builderFor(connection).build();
        this.properties = properties;
    }

    private Bucket resolveBucket(String clientKey, String httpMethod) {
        RateLimiterProperties.Limit limit = properties.getLimits().getOrDefault(httpMethod.toUpperCase(), null);

        if (limit == null) {
            // Fallback: no limits
            Bandwidth unlimited = Bandwidth.builder()
                    .capacity(Long.MAX_VALUE)
                    .refillIntervally(Long.MAX_VALUE, Duration.ofSeconds(1))
                    .build();

            return Bucket.builder().addLimit(unlimited).build();
        }

        Supplier<BucketConfiguration> configSupplier = () -> {
            Bandwidth bandwidth = Bandwidth.builder()
                    .capacity(limit.getCapacity())
                    .refillIntervally(limit.getCapacity(), Duration.ofSeconds(limit.getDuration()))
                    .build();

            return new BucketConfiguration(Collections.singletonList(bandwidth));
        };

        return proxyManager.getProxy(clientKey + ":" + httpMethod, configSupplier);
    }

    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String clientKey = request.getRemoteAddr();

        Bucket bucket = resolveBucket(clientKey, method);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many " + method + " requests. Try again later.");
        }
    }
}