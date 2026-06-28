package com.mall.gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.security.AuthHeaderNames;
import com.mall.common.security.LoginTokenCodec;
import com.mall.common.security.LoginTokenDTO;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class AuthHeaderRelayFilterTest {

    private final LoginTokenCodec loginTokenCodec =
            new LoginTokenCodec(new ObjectMapper(), "test-secret-32-characters-value");
    private final AuthHeaderRelayFilter filter = new AuthHeaderRelayFilter(loginTokenCodec);

    @Test
    void filter_withoutValidToken_removesExternalMallHeaders() {
        final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/wms/inbound/orders")
                .header(AuthHeaderNames.ACCOUNT_ID, "999999")
                .header(AuthHeaderNames.AUTHORITIES, "wms:inbound:view")
                .build());
        final AtomicReference<ServerWebExchange> forwardedExchange = new AtomicReference<>();

        filter.filter(exchange, nextExchange -> {
            forwardedExchange.set(nextExchange);
            return Mono.empty();
        }).block();

        assertNull(forwardedExchange.get().getRequest().getHeaders().getFirst(AuthHeaderNames.ACCOUNT_ID));
        assertNull(forwardedExchange.get().getRequest().getHeaders().getFirst(AuthHeaderNames.AUTHORITIES));
    }

    @Test
    void filter_withSignedToken_setsMallHeadersFromToken() {
        final LoginTokenDTO tokenDTO = new LoginTokenDTO();
        tokenDTO.setAccountId(700007L);
        tokenDTO.setUsername("test_wms_supervisor");
        tokenDTO.setPortalCode("wms-admin");
        tokenDTO.setAuthorities(List.of("wms:inbound:view", "wms:delivery:view"));
        tokenDTO.setWarehouseIds(List.of(50001L));
        final String token = loginTokenCodec.encode(tokenDTO);
        final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/wms/inbound/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(AuthHeaderNames.ACCOUNT_ID, "999999")
                .build());
        final AtomicReference<ServerWebExchange> forwardedExchange = new AtomicReference<>();

        filter.filter(exchange, nextExchange -> {
            forwardedExchange.set(nextExchange);
            return Mono.empty();
        }).block();

        assertEquals("700007", forwardedExchange.get().getRequest().getHeaders().getFirst(AuthHeaderNames.ACCOUNT_ID));
        assertEquals("wms-admin", forwardedExchange.get().getRequest().getHeaders().getFirst(AuthHeaderNames.PORTAL_CODE));
        assertEquals("50001", forwardedExchange.get().getRequest().getHeaders().getFirst(AuthHeaderNames.WAREHOUSE_IDS));
    }
}
