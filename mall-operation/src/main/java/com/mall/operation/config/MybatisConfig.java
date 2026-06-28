package com.mall.operation.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.operation.mapper")
public class MybatisConfig {
}
