package com.mall.common.security;

import java.io.Serializable;

public class MallLoginUser implements Serializable {

    private Long accountId;
    private String username;
    private String portalCode;

    public MallLoginUser(final Long accountId, final String username) {
        this(accountId, username, null);
    }

    public MallLoginUser(final Long accountId, final String username, final String portalCode) {
        this.accountId = accountId;
        this.username = username;
        this.portalCode = portalCode;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(final String portalCode) {
        this.portalCode = portalCode;
    }
}
