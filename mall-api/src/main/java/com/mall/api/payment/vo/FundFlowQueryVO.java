package com.mall.api.payment.vo;

import com.mall.common.page.PageQuery;

public class FundFlowQueryVO extends PageQuery {

    private String keyword;
    private Long flowType;
    private String bizNo;
    private Long direction;
    private Long subjectType;
    private Long subjectId;
    private Long status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getFlowType() {
        return flowType;
    }

    public void setFlowType(final Long flowType) {
        this.flowType = flowType;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(final String bizNo) {
        this.bizNo = bizNo;
    }

    public Long getDirection() {
        return direction;
    }

    public void setDirection(final Long direction) {
        this.direction = direction;
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
}
