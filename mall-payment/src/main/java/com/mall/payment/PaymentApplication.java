package com.mall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.payment", "com.mall.common"})
public class PaymentApplication {

    public static void main(final String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
