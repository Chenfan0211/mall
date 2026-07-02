package com.mall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.user", "com.mall.common"})
public class UserApplication {

    public static void main(final String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
