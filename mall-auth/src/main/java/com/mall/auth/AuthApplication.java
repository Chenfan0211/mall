package com.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.mall.auth",
        "com.mall.common",
        "com.mall.system.convert",
        "com.mall.system.service"
})
public class AuthApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
