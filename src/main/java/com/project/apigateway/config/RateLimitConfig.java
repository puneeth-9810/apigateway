package com.project.apigateway.config;


import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

public class RateLimitConfig {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ipaddress_route", p -> p
                        .path("/example2")
                        .filters(f -> f.requestRateLimiter(r -> r.setRateLimiter(redisRateLimiter())
                                .setDenyEmptyKey(false)
                                .setKeyResolver(new SimpleClientAddressResolver())))
                        .uri("http://example.org"))
                .build();
    }


    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }
}
