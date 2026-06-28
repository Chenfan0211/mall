package com.mall.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class GatewayAuthenticationFilterTest {

    @Test
    void doFilterInternal_withGatewayHeaders_setAuthenticationAndDataScope() throws ServletException, IOException {
        final GatewayAuthenticationFilter filter = new GatewayAuthenticationFilter();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AuthHeaderNames.ACCOUNT_ID, "700001");
        request.addHeader(AuthHeaderNames.USERNAME, "test_platform_admin");
        request.addHeader(AuthHeaderNames.PORTAL_CODE, "admin");
        request.addHeader(AuthHeaderNames.AUTHORITIES, "operation:todo:view,operation:exception:view");
        request.addHeader(AuthHeaderNames.ALL_PLATFORM, "true");
        request.addHeader(AuthHeaderNames.CITY_IDS, "440100,440300");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = (final ServletRequest servletRequest, final ServletResponse servletResponse) -> {
            final MallLoginUser loginUser =
                    (MallLoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            assertEquals(700001L, loginUser.getAccountId());
            assertEquals("admin", loginUser.getPortalCode());
            assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(authority -> "operation:todo:view".equals(authority.getAuthority())));
            assertTrue(DataScopeContext.get().isAllPlatform());
            assertTrue(DataScopeContext.get().getCityIds().contains(440100L));
        };

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNull(DataScopeContext.get());
    }
}
