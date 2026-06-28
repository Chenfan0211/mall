package com.mall.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.security.LoginTokenCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginTokenCodec loginTokenCodec(
            final ObjectMapper objectMapper,
            @Value("${mall.security.token-secret}") final String tokenSecret) {
        return new LoginTokenCodec(objectMapper, tokenSecret);
    }

    @Bean
    public SecurityFilterChain authSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(registry -> registry.anyRequest().permitAll());
        return http.build();
    }
}
