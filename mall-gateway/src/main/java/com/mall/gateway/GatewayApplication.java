package com.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.gateway", "com.mall.common"})
public class GatewayApplication {

    public static void main(final String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
