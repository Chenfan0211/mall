package com.mall.common.security;

import java.util.Arrays;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public class PortalAuthorization {

    private static final String ADMIN_PORTAL = "admin";
    private static final String WMS_ADMIN_PORTAL = "wms-admin";

    public PortalAuthorization() {
    }

    public boolean isPortal(final String portalCode) {
        if (!StringUtils.hasText(portalCode)) {
            return false;
        }
        final MallLoginUser loginUser = currentLoginUser();
        return loginUser != null && portalCode.equals(loginUser.getPortalCode());
    }

    public boolean isAnyPortal(final String... portalCodes) {
        if (portalCodes == null || portalCodes.length == 0) {
            return false;
        }
        final MallLoginUser loginUser = currentLoginUser();
        if (loginUser == null || !StringUtils.hasText(loginUser.getPortalCode())) {
            return false;
        }
        return Arrays.asList(portalCodes).contains(loginUser.getPortalCode());
    }

    public boolean isBackendPortal() {
        return isAnyPortal(ADMIN_PORTAL, WMS_ADMIN_PORTAL);
    }

    public boolean isMobilePortal() {
        return !isBackendPortal() && currentLoginUser() != null;
    }

    private static MallLoginUser currentLoginUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof MallLoginUser loginUser)) {
            return null;
        }
        return loginUser;
    }
}
