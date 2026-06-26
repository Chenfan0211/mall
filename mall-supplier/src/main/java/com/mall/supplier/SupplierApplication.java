package com.mall.supplier;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mall.supplier.mapper")
public class SupplierApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SupplierApplication.class, args);
    }
}
