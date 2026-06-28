package com.mall.sale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.sale", "com.mall.common"})
public class SaleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SaleApplication.class, args);
    }
}
