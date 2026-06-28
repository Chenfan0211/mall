package com.mall.aftersale.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.aftersale.mapper")
public class MybatisConfig {
}
