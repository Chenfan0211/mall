package com.mall.wms.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.wms.mapper")
public class MybatisConfig {
}
