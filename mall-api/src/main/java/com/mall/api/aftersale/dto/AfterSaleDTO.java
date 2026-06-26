package com.mall.api.aftersale.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AfterSaleDTO {

    private Long id;
    private String afterSaleNo;
    private Long orderId;
    private String orderNo;
    private Long userId;
    private Long cityId;
    private Long stationId;
    private Long afterSaleType;
    private Long status;
    private String applyReason;
    private BigDecimal refundAmount;
    private Long responsibilityType;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(final String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getAfterSaleType() {
        return afterSaleType;
    }

    public void setAfterSaleType(final Long afterSaleType) {
        this.afterSaleType = afterSaleType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(final String applyReason) {
        this.applyReason = applyReason;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(final BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Long getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(final Long responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
