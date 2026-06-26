package com.mall.common.security;

import java.io.Serializable;

public class MallLoginUser implements Serializable {

    private Long accountId;
    private String username;

    public MallLoginUser(final Long accountId, final String username) {
        this.accountId = accountId;
        this.username = username;
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
}
