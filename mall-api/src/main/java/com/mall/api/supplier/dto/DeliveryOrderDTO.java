package com.mall.api.supplier.dto;

import java.time.LocalDateTime;

public class DeliveryOrderDTO {

    private Long id;
    private String deliveryNo;
    private Long purchaseId;
    private String purchaseNo;
    private Long supplierId;
    private Long warehouseId;
    private LocalDateTime expectedArrivalTime;
    private String arrivalProofJson;
    private String contactName;
    private String contactMobile;
    private String vehicleNo;
    private Long status;
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

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(final Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseNo() {
        return purchaseNo;
    }

    public void setPurchaseNo(final String purchaseNo) {
        this.purchaseNo = purchaseNo;
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

    public LocalDateTime getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(final LocalDateTime expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public String getArrivalProofJson() {
        return arrivalProofJson;
    }

    public void setArrivalProofJson(final String arrivalProofJson) {
        this.arrivalProofJson = arrivalProofJson;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(final String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(final String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(final String vehicleNo) {
        this.vehicleNo = vehicleNo;
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
