package com.mall.api.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SplitFlowDTO {

    private Long id;
    private String splitNo;
    private Long commissionId;
    private Long orderId;
    private Long subjectType;
    private Long subjectId;
    private BigDecimal splitAmount;
    private Long splitStatus;
    private String failReason;
    private String idempotentKey;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSplitNo() {
        return splitNo;
    }

    public void setSplitNo(final String splitNo) {
        this.splitNo = splitNo;
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

    public BigDecimal getSplitAmount() {
        return splitAmount;
    }

    public void setSplitAmount(final BigDecimal splitAmount) {
        this.splitAmount = splitAmount;
    }

    public Long getSplitStatus() {
        return splitStatus;
    }

    public void setSplitStatus(final Long splitStatus) {
        this.splitStatus = splitStatus;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(final String failReason) {
        this.failReason = failReason;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(final String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
