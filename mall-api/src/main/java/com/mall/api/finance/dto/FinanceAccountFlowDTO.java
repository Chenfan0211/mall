package com.mall.api.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinanceAccountFlowDTO {

    private Long id;
    private String flowNo;
    private Long accountId;
    private Long subjectType;
    private Long subjectId;
    private Long flowType;
    private Long direction;
    private BigDecimal amount;
    private BigDecimal beforeAmount;
    private BigDecimal afterAmount;
    private String bizNo;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(final String flowNo) {
        this.flowNo = flowNo;
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

    public Long getFlowType() {
        return flowType;
    }

    public void setFlowType(final Long flowType) {
        this.flowType = flowType;
    }

    public Long getDirection() {
        return direction;
    }

    public void setDirection(final Long direction) {
        this.direction = direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBeforeAmount() {
        return beforeAmount;
    }

    public void setBeforeAmount(final BigDecimal beforeAmount) {
        this.beforeAmount = beforeAmount;
    }

    public BigDecimal getAfterAmount() {
        return afterAmount;
    }

    public void setAfterAmount(final BigDecimal afterAmount) {
        this.afterAmount = afterAmount;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(final String bizNo) {
        this.bizNo = bizNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
