package com.mall.aftersale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.aftersale", "com.mall.common"})
@MapperScan("com.mall.aftersale.mapper")
public class AfterSaleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AfterSaleApplication.class, args);
    }
}
