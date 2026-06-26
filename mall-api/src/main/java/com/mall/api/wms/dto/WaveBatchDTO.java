package com.mall.api.wms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WaveBatchDTO {

    private Long id;
    private String waveNo;
    private Long periodId;
    private Long warehouseId;
    private Long lineId;
    private LocalDate deliveryDate;
    private Long requiredQty;
    private Long lockedQty;
    private Long status;
    private Long createAccountId;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getWaveNo() {
        return waveNo;
    }

    public void setWaveNo(final String waveNo) {
        this.waveNo = waveNo;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(final Long periodId) {
        this.periodId = periodId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(final Long lineId) {
        this.lineId = lineId;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getRequiredQty() {
        return requiredQty;
    }

    public void setRequiredQty(final Long requiredQty) {
        this.requiredQty = requiredQty;
    }

    public Long getLockedQty() {
        return lockedQty;
    }

    public void setLockedQty(final Long lockedQty) {
        this.lockedQty = lockedQty;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getCreateAccountId() {
        return createAccountId;
    }

    public void setCreateAccountId(final Long createAccountId) {
        this.createAccountId = createAccountId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
