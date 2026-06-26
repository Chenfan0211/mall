package com.mall.operation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mall.operation.mapper")
@SpringBootApplication(scanBasePackages = {"com.mall.operation", "com.mall.common"})
public class OperationApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OperationApplication.class, args);
    }
}
