package com.mall.api.trade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTO {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long regionId;
    private Long cityId;
    private Long stationId;
    private Long leaderId;
    private String pickupName;
    private String pickupMobile;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Long tradeStatus;
    private Long payStatus;
    private Long fulfillStatus;
    private LocalDateTime expireTime;
    private LocalDateTime payTime;
    private LocalDateTime completeTime;
    private String cancelReason;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(final Long regionId) {
        this.regionId = regionId;
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

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(final Long leaderId) {
        this.leaderId = leaderId;
    }

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(final String pickupName) {
        this.pickupName = pickupName;
    }

    public String getPickupMobile() {
        return pickupMobile;
    }

    public void setPickupMobile(final String pickupMobile) {
        this.pickupMobile = pickupMobile;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(final BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Long getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(final Long tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Long getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(final Long payStatus) {
        this.payStatus = payStatus;
    }

    public Long getFulfillStatus() {
        return fulfillStatus;
    }

    public void setFulfillStatus(final Long fulfillStatus) {
        this.fulfillStatus = fulfillStatus;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(final LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(final LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(final LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(final String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
