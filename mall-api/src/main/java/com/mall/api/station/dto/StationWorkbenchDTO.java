package com.mall.api.station.dto;

import java.math.BigDecimal;

public class StationWorkbenchDTO {

    private Long stationId;
    private Long leaderId;
    private Long todayOrderCount;
    private Long waitPickupCount;
    private Long pickedCount;
    private Long arrivalCount;
    private Long exceptionCount;
    private Long unreadMessageCount;
    private BigDecimal availableAmount;
    private BigDecimal frozenAmount;
    private BigDecimal pendingCommissionAmount;

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

    public Long getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(final Long todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public Long getWaitPickupCount() {
        return waitPickupCount;
    }

    public void setWaitPickupCount(final Long waitPickupCount) {
        this.waitPickupCount = waitPickupCount;
    }

    public Long getPickedCount() {
        return pickedCount;
    }

    public void setPickedCount(final Long pickedCount) {
        this.pickedCount = pickedCount;
    }

    public Long getArrivalCount() {
        return arrivalCount;
    }

    public void setArrivalCount(final Long arrivalCount) {
        this.arrivalCount = arrivalCount;
    }

    public Long getExceptionCount() {
        return exceptionCount;
    }

    public void setExceptionCount(final Long exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    public Long getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(final Long unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(final BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(final BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public BigDecimal getPendingCommissionAmount() {
        return pendingCommissionAmount;
    }

    public void setPendingCommissionAmount(final BigDecimal pendingCommissionAmount) {
        this.pendingCommissionAmount = pendingCommissionAmount;
    }
}
