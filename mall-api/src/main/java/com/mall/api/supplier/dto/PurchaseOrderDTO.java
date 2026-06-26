package com.mall.api.supplier.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseOrderDTO {

    private Long id;
    private String purchaseNo;
    private Long sourceType;
    private Long periodId;
    private Long supplierId;
    private Long warehouseId;
    private Long regionId;
    private Long cityId;
    private LocalDate deliveryDate;
    private Long purchaseStatus;
    private Long auditStatus;
    private Long createAccountId;
    private String createReason;
    private LocalDateTime expectedArrivalTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getPurchaseNo() {
        return purchaseNo;
    }

    public void setPurchaseNo(final String purchaseNo) {
        this.purchaseNo = purchaseNo;
    }

    public Long getSourceType() {
        return sourceType;
    }

    public void setSourceType(final Long sourceType) {
        this.sourceType = sourceType;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(final Long periodId) {
        this.periodId = periodId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
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

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(final Long purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getCreateAccountId() {
        return createAccountId;
    }

    public void setCreateAccountId(final Long createAccountId) {
        this.createAccountId = createAccountId;
    }

    public String getCreateReason() {
        return createReason;
    }

    public void setCreateReason(final String createReason) {
        this.createReason = createReason;
    }

    public LocalDateTime getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(final LocalDateTime expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
