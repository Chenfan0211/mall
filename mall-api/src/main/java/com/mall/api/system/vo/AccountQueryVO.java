package com.mall.api.system.vo;

import com.mall.common.page.PageQuery;

public class AccountQueryVO extends PageQuery {

    private String keyword;
    private String loginPortalCode;
    private Long accountType;
    private Long status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public String getLoginPortalCode() {
        return loginPortalCode;
    }

    public void setLoginPortalCode(final String loginPortalCode) {
        this.loginPortalCode = loginPortalCode;
    }

    public Long getAccountType() {
        return accountType;
    }

    public void setAccountType(final Long accountType) {
        this.accountType = accountType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
