package com.mall.api.finance.vo;

import com.mall.common.page.PageQuery;

public class SplitFlowQueryVO extends PageQuery {

    private String keyword;
    private Long commissionId;
    private Long orderId;
    private Long subjectType;
    private Long subjectId;
    private Long splitStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(final Long commissionId) {
        this.commissionId = commissionId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
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

    public Long getSplitStatus() {
        return splitStatus;
    }

    public void setSplitStatus(final Long splitStatus) {
        this.splitStatus = splitStatus;
    }
}
