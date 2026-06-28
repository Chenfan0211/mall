package com.mall.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.security.LoginTokenCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayTokenConfig {

    @Bean
    public LoginTokenCodec loginTokenCodec(
            final ObjectMapper objectMapper,
            @Value("${mall.security.token-secret}") final String tokenSecret) {
        return new LoginTokenCodec(objectMapper, tokenSecret);
    }
}
