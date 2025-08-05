package com.code.spring.taskmanagement.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


@Getter
@Setter
@ConfigurationProperties(prefix = "bucket4j")
public class RateLimiterProperties {
    private String redisUrl;
    private Map<String, Limit> limits;

    @Getter
    @Setter
    public static class Limit {
        private int capacity;
        private int duration;
    }
}
