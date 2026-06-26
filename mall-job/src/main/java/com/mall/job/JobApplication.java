package com.mall.job;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.job", "com.mall.common"})
@MapperScan("com.mall.job.mapper")
public class JobApplication {

    public static void main(final String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}
