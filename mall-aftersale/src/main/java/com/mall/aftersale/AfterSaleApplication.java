package com.mall.aftersale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.aftersale", "com.mall.common"})
public class AfterSaleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AfterSaleApplication.class, args);
    }
}
