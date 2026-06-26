package com.mall.station;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mall.station.mapper")
public class StationApplication {

    public static void main(final String[] args) {
        SpringApplication.run(StationApplication.class, args);
    }
}
