package com.mall.api.system.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AccountDTO implements Serializable {

    private Long id;
    private String accountNo;
    private String username;
    private String mobile;
    private Long accountType;
    private String loginPortalCode;
    private Long subjectType;
    private Long subjectId;
    private Long status;
    private LocalDateTime lastLoginTime;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(final String accountNo) {
        this.accountNo = accountNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public Long getAccountType() {
        return accountType;
    }

    public void setAccountType(final Long accountType) {
        this.accountType = accountType;
    }

    public String getLoginPortalCode() {
        return loginPortalCode;
    }

    public void setLoginPortalCode(final String loginPortalCode) {
        this.loginPortalCode = loginPortalCode;
    }

    public Long getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(final Long subjectType) {
        this.subjectType = subjectType;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(final Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(final LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }
}
