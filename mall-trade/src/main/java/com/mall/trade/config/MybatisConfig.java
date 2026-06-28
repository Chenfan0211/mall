package com.mall.trade.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.trade.mapper")
public class MybatisConfig {
}
