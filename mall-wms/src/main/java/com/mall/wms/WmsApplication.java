package com.mall.wms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mall.wms.mapper")
public class WmsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WmsApplication.class, args);
    }
}
