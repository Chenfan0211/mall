package com.mall.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mall")
public class SystemApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
