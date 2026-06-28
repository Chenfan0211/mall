package com.mall.common.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.util.StringUtils;

public class LoginTokenCodec {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final String secret;

    public LoginTokenCodec(final ObjectMapper objectMapper, final String secret) {
        this.objectMapper = objectMapper;
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("登录令牌签名密钥不能为空");
        }
        this.secret = secret;
    }

    public String encode(final LoginTokenDTO tokenDTO) {
        try {
            final TokenPayload payload = new TokenPayload();
            payload.setLogin(tokenDTO);
            payload.setIssuedAt(Instant.now().toEpochMilli());
            final String payloadText = base64Url(objectMapper.writeValueAsBytes(payload));
            return payloadText + "." + sign(payloadText);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("登录令牌生成失败", exception);
        }
    }

    public LoginTokenDTO decode(final String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        final String[] parts = token.split("\\.");
        if (parts.length != 2 || !sign(parts[0]).equals(parts[1])) {
            return null;
        }
        try {
            final byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[0]);
            final TokenPayload payload = objectMapper.readValue(payloadBytes, TokenPayload.class);
            return payload == null ? null : payload.getLogin();
        } catch (IllegalArgumentException | IOException exception) {
            return null;
        }
    }

    private String sign(final String payload) {
        try {
            final Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return base64Url(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new IllegalStateException("登录令牌签名失败", exception);
        }
    }

    private String base64Url(final byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static class TokenPayload {

        private Long issuedAt;
        private LoginTokenDTO login;

        public Long getIssuedAt() {
            return issuedAt;
        }

        public void setIssuedAt(final Long issuedAt) {
            this.issuedAt = issuedAt;
        }

        public LoginTokenDTO getLogin() {
            return login;
        }

        public void setLogin(final LoginTokenDTO login) {
            this.login = login;
        }
    }
}
