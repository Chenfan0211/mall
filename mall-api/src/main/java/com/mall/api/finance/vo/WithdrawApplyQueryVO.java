package com.mall.api.finance.vo;

import com.mall.common.page.PageQuery;

public class WithdrawApplyQueryVO extends PageQuery {

    private String keyword;
    private Long accountId;
    private Long subjectType;
    private Long subjectId;
    private Long auditStatus;
    private Long payStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
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

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(final Long payStatus) {
        this.payStatus = payStatus;
    }
}
