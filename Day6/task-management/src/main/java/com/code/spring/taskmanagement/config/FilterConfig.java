package com.code.spring.taskmanagement.config;

import com.code.spring.taskmanagement.filter.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.lettuce.core.api.StatefulRedisConnection;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(StatefulRedisConnection<String, byte[]> redisConnection,
                                                                   RateLimiterProperties rateLimiterProperties) {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter(redisConnection, rateLimiterProperties));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
