package com.mall.api.wms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoadingOrderDTO {

    private Long id;
    private String loadingNo;
    private Long waveId;
    private Long warehouseId;
    private Long lineId;
    private Long driverAccountId;
    private LocalDate deliveryDate;
    private Long requiredQty;
    private Long actualQty;
    private Long status;
    private Long loadAccountId;
    private LocalDateTime loadTime;
    private Long outboundAccountId;
    private LocalDateTime outboundTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLoadingNo() {
        return loadingNo;
    }

    public void setLoadingNo(final String loadingNo) {
        this.loadingNo = loadingNo;
    }

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(final Long waveId) {
        this.waveId = waveId;
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

    public Long getDriverAccountId() {
        return driverAccountId;
    }

    public void setDriverAccountId(final Long driverAccountId) {
        this.driverAccountId = driverAccountId;
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

    public Long getActualQty() {
        return actualQty;
    }

    public void setActualQty(final Long actualQty) {
        this.actualQty = actualQty;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getLoadAccountId() {
        return loadAccountId;
    }

    public void setLoadAccountId(final Long loadAccountId) {
        this.loadAccountId = loadAccountId;
    }

    public LocalDateTime getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(final LocalDateTime loadTime) {
        this.loadTime = loadTime;
    }

    public Long getOutboundAccountId() {
        return outboundAccountId;
    }

    public void setOutboundAccountId(final Long outboundAccountId) {
        this.outboundAccountId = outboundAccountId;
    }

    public LocalDateTime getOutboundTime() {
        return outboundTime;
    }

    public void setOutboundTime(final LocalDateTime outboundTime) {
        this.outboundTime = outboundTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
