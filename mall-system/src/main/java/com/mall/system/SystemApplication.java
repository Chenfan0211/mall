package com.mall.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mall.system.mapper")
@SpringBootApplication(scanBasePackages = "com.mall")
public class SystemApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
