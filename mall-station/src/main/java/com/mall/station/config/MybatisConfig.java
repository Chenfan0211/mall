package com.mall.station.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.station.mapper")
public class MybatisConfig {
}
