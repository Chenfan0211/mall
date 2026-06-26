package com.mall.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;

@TableName("pay_fund_flow")
public class PayFundFlow extends BaseEntity {

    private String flowNo;
    private Long flowType;
    private String bizNo;
    private BigDecimal amount;
    private Long direction;
    private Long subjectType;
    private Long subjectId;
    private Long status;
    private String channelFlowNo;

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
}
