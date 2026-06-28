package com.mall.supplier.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.supplier.mapper")
public class MybatisConfig {
}
