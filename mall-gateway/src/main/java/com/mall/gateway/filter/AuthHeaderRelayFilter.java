package com.mall.gateway.filter;

import com.mall.common.security.AuthHeaderNames;
import com.mall.common.security.LoginTokenCodec;
import com.mall.common.security.LoginTokenDTO;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthHeaderRelayFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final List<String> AUTH_HEADER_NAMES = List.of(
            AuthHeaderNames.ACCOUNT_ID,
            AuthHeaderNames.USERNAME,
            AuthHeaderNames.PORTAL_CODE,
            AuthHeaderNames.AUTHORITIES,
            AuthHeaderNames.ALL_PLATFORM,
            AuthHeaderNames.CITY_IDS,
            AuthHeaderNames.SUPPLIER_IDS,
            AuthHeaderNames.STATION_IDS,
            AuthHeaderNames.WAREHOUSE_IDS,
            AuthHeaderNames.DRIVER_IDS,
            AuthHeaderNames.LEADER_IDS);

    private final LoginTokenCodec loginTokenCodec;

    public AuthHeaderRelayFilter(final LoginTokenCodec loginTokenCodec) {
        this.loginTokenCodec = loginTokenCodec;
    }

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final LoginTokenDTO tokenDTO = loginTokenCodec.decode(resolveBearerToken(exchange.getRequest()));
        final ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    AUTH_HEADER_NAMES.forEach(headers::remove);
                    if (tokenDTO == null || tokenDTO.getAccountId() == null) {
                        return;
                    }
                    headers.set(AuthHeaderNames.ACCOUNT_ID, String.valueOf(tokenDTO.getAccountId()));
                    headers.set(AuthHeaderNames.USERNAME, safeText(tokenDTO.getUsername()));
                    headers.set(AuthHeaderNames.PORTAL_CODE, safeText(tokenDTO.getPortalCode()));
                    headers.set(AuthHeaderNames.AUTHORITIES, join(tokenDTO.getAuthorities()));
                    headers.set(AuthHeaderNames.ALL_PLATFORM, String.valueOf(tokenDTO.isAllPlatform()));
                    headers.set(AuthHeaderNames.CITY_IDS, joinLongs(tokenDTO.getCityIds()));
                    headers.set(AuthHeaderNames.SUPPLIER_IDS, joinLongs(tokenDTO.getSupplierIds()));
                    headers.set(AuthHeaderNames.STATION_IDS, joinLongs(tokenDTO.getStationIds()));
                    headers.set(AuthHeaderNames.WAREHOUSE_IDS, joinLongs(tokenDTO.getWarehouseIds()));
                    headers.set(AuthHeaderNames.DRIVER_IDS, joinLongs(tokenDTO.getDriverIds()));
                    headers.set(AuthHeaderNames.LEADER_IDS, joinLongs(tokenDTO.getLeaderIds()));
                })
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    private String resolveBearerToken(final ServerHttpRequest request) {
        final String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorization.substring(BEARER_PREFIX.length());
    }

    private String safeText(final String value) {
        return value == null ? "" : value;
    }

    private String join(final List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return "";
        }
        return String.join(",", values);
    }

    private String joinLongs(final List<Long> values) {
        if (CollectionUtils.isEmpty(values)) {
            return "";
        }
        return String.join(",", values.stream()
                .filter(value -> value != null)
                .map(String::valueOf)
                .toList());
    }
}
