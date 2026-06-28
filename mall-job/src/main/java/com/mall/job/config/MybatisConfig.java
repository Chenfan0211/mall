package com.mall.job.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.job.mapper")
public class MybatisConfig {
}
