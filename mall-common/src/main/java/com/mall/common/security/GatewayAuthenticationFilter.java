package com.mall.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        try {
            setAuthentication(request);
            filterChain.doFilter(request, response);
        } finally {
            DataScopeContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthentication(final HttpServletRequest request) {
        final Long accountId = parseLong(request.getHeader(AuthHeaderNames.ACCOUNT_ID));
        if (accountId == null) {
            return;
        }
        final String username = request.getHeader(AuthHeaderNames.USERNAME);
        final String portalCode = request.getHeader(AuthHeaderNames.PORTAL_CODE);
        final MallLoginUser loginUser = new MallLoginUser(accountId, username, portalCode);
        final Set<SimpleGrantedAuthority> authorities = split(request.getHeader(AuthHeaderNames.AUTHORITIES)).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        DataScopeContext.set(buildDataScope(request, accountId));
    }

    private DataScopeContext.DataScope buildDataScope(final HttpServletRequest request, final Long accountId) {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setAccountId(accountId);
        dataScope.setAllPlatform(Boolean.parseBoolean(request.getHeader(AuthHeaderNames.ALL_PLATFORM)));
        dataScope.setCityIds(parseLongSet(request.getHeader(AuthHeaderNames.CITY_IDS)));
        dataScope.setSupplierIds(parseLongSet(request.getHeader(AuthHeaderNames.SUPPLIER_IDS)));
        dataScope.setStationIds(parseLongSet(request.getHeader(AuthHeaderNames.STATION_IDS)));
        dataScope.setWarehouseIds(parseLongSet(request.getHeader(AuthHeaderNames.WAREHOUSE_IDS)));
        dataScope.setDriverIds(parseLongSet(request.getHeader(AuthHeaderNames.DRIVER_IDS)));
        dataScope.setLeaderIds(parseLongSet(request.getHeader(AuthHeaderNames.LEADER_IDS)));
        return dataScope;
    }

    private Set<Long> parseLongSet(final String value) {
        return split(value).stream()
                .map(this::parseLong)
                .filter(item -> item != null)
                .collect(Collectors.toSet());
    }

    private Long parseLong(final String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Set<String> split(final String value) {
        if (!StringUtils.hasText(value)) {
            return Collections.emptySet();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }
}
