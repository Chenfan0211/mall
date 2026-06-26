package com.mall.api.system.vo;

import com.mall.common.page.PageQuery;

public class RoleQueryVO extends PageQuery {

    private String portalCode;
    private Long status;

    public String getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(final String portalCode) {
        this.portalCode = portalCode;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
