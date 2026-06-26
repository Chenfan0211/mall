package com.mall.api.wms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeliveryOrderDTO {

    private Long id;
    private String deliveryNo;
    private Long outboundId;
    private Long loadingId;
    private Long warehouseId;
    private Long lineId;
    private Long driverAccountId;
    private LocalDate deliveryDate;
    private Long status;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(final String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    public Long getOutboundId() {
        return outboundId;
    }

    public void setOutboundId(final Long outboundId) {
        this.outboundId = outboundId;
    }

    public Long getLoadingId() {
        return loadingId;
    }

    public void setLoadingId(final Long loadingId) {
        this.loadingId = loadingId;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(final LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
