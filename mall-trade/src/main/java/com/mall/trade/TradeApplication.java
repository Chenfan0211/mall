package com.mall.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.trade", "com.mall.common"})
@MapperScan("com.mall.trade.mapper")
public class TradeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }
}
