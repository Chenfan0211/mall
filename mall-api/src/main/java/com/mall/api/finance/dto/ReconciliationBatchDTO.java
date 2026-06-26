package com.mall.api.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReconciliationBatchDTO {

    private Long id;
    private String reconcileNo;
    private Long channelType;
    private LocalDate reconcileDate;
    private Long totalCount;
    private Long successCount;
    private Long diffCount;
    private BigDecimal totalAmount;
    private BigDecimal diffAmount;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getReconcileNo() {
        return reconcileNo;
    }

    public void setReconcileNo(final String reconcileNo) {
        this.reconcileNo = reconcileNo;
    }

    public Long getChannelType() {
        return channelType;
    }

    public void setChannelType(final Long channelType) {
        this.channelType = channelType;
    }

    public LocalDate getReconcileDate() {
        return reconcileDate;
    }

    public void setReconcileDate(final LocalDate reconcileDate) {
        this.reconcileDate = reconcileDate;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(final Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(final Long successCount) {
        this.successCount = successCount;
    }

    public Long getDiffCount() {
        return diffCount;
    }

    public void setDiffCount(final Long diffCount) {
        this.diffCount = diffCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiffAmount() {
        return diffAmount;
    }

    public void setDiffAmount(final BigDecimal diffAmount) {
        this.diffAmount = diffAmount;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
