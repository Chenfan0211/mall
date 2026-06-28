package com.mall.finance.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.finance.mapper")
public class MybatisConfig {
}
