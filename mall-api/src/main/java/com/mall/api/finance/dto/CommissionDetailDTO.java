package com.mall.api.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommissionDetailDTO {

    private Long id;
    private String commissionNo;
    private Long orderId;
    private Long orderItemId;
    private Long subjectType;
    private Long subjectId;
    private Long supplierId;
    private Long stationId;
    private BigDecimal commissionAmount;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCommissionNo() {
        return commissionNo;
    }

    public void setCommissionNo(final String commissionNo) {
        this.commissionNo = commissionNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(final Long orderItemId) {
        this.orderItemId = orderItemId;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(final BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
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
