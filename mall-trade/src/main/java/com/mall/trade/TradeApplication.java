package com.mall.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.trade", "com.mall.common"})
public class TradeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }
}
