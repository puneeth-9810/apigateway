package com.project.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("rate_limiter_route", r -> r
                        .path("/redis/get/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())))
                        .uri("https://httpbin.org"))

                .route("retry_test", r -> r
                        .path("/status/502")
                        .filters(f -> f.retry(config -> {
                            config.setRetries(3);
                            config.setStatuses(HttpStatus.BAD_GATEWAY);
                            config.setMethods(HttpMethod.GET, HttpMethod.POST);
                            config.setBackoff(Duration.ofMillis(10), Duration.ofMillis(50), 2, false);
                        }))
                        .uri("https://httpbin.org"))

                .route("circuitbreaker_route", r -> r
                        .path("/status/504")
                        .filters(f -> f.circuitBreaker(config -> {
                                    config.setName("myCircuitBreaker");
                                    config.setFallbackUri("forward:/anything");
                                })
                                .rewritePath("/status/504", "/anything"))
                        .uri("https://httpbin.org"))

                .route("redirect_route", r -> r
                        .path("/fake/post/**")
                        .filters(f -> f.redirect(302, "https://httpbin.org"))
                        .uri("https://httpbin.org"))

                .route("status_route", r -> r
                        .path("/delete/**")
                        .filters(f -> f.setStatus(HttpStatus.UNAUTHORIZED))
                        .uri("https://httpbin.org"))

                .route("path_route", r -> r
                        .path("/new/post/**")
                        .filters(f -> f.rewritePath("/new(?<segment>/?.*)", "${segment}")
                                .setPath("/post"))
                        .uri("https://httpbin.org"))

                .route("add_request_header_route", r -> r
                        .path("/get/**")
                        .filters(f -> f.addRequestHeader("My-Header-Good", "Good")
                                .addRequestHeader("My-Header-Remove", "Remove")
                                .addRequestHeadersIfNotPresent("My-Header-Absent", "Absent")
                                .addRequestParameter("var", "good")
                                .addRequestParameter("var2", "remove")
                                .mapRequestHeader("My-Header-Good", "My-Header-Bad")
                                .mapRequestHeader("My-Header-Set", "My-Header-Bad")
                                .setRequestHeader("My-Header-Set", "Set")
                                .removeRequestHeader("My-Header-Remove")
                                .removeRequestParameter("var2"))
                        .uri("https://httpbin.org"))

                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 5, 1);
    }
}
