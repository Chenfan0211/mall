package com.mall.sale.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.sale.mapper")
public class MybatisConfig {
}
