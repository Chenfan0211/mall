package com.mall.common.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class PortalAuthorizationTest {

    private final PortalAuthorization portalAuthorization = new PortalAuthorization();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isBackendPortal_adminPortal_returnsTrue() {
        setLoginUser("admin");

        assertTrue(portalAuthorization.isBackendPortal());
        assertTrue(portalAuthorization.isPortal("admin"));
        assertFalse(portalAuthorization.isMobilePortal());
    }

    @Test
    void isMobilePortal_warehouseH5Portal_returnsTrue() {
        setLoginUser("warehouse-h5");

        assertTrue(portalAuthorization.isMobilePortal());
        assertTrue(portalAuthorization.isAnyPortal("role-workbench-h5", "warehouse-h5"));
        assertFalse(portalAuthorization.isBackendPortal());
    }

    private void setLoginUser(final String portalCode) {
        final MallLoginUser loginUser = new MallLoginUser(700001L, "test_account", portalCode);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null));
    }
}
