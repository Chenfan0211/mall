package com.mall.sale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.sale", "com.mall.common"})
@MapperScan("com.mall.sale.mapper")
public class SaleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SaleApplication.class, args);
    }
}
