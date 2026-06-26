package com.mall.api.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FundFlowDTO {

    private Long id;
    private String flowNo;
    private Long flowType;
    private String bizNo;
    private BigDecimal amount;
    private Long direction;
    private Long subjectType;
    private Long subjectId;
    private Long status;
    private String channelFlowNo;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
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

    public String getChannelFlowNo() {
        return channelFlowNo;
    }

    public void setChannelFlowNo(final String channelFlowNo) {
        this.channelFlowNo = channelFlowNo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
