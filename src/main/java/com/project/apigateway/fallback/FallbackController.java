package com.project.apigateway.fallback;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @RequestMapping("/fallback/users")
    public String userFallback() {
        return "User Service is currently unavailable.";
    }

    @RequestMapping("/fallback/orders")
    public String orderFallback() {
        return "Order Service is currently unavailable.";
    }
}
