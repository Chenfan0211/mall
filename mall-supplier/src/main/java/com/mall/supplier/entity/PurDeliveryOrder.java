package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("pur_delivery_order")
public class PurDeliveryOrder extends BaseEntity {

    private String deliveryNo;
    private Long purchaseId;
    private String purchaseNo;
    private Long supplierId;
    private Long warehouseId;
    private LocalDateTime expectedArrivalTime;
    @TableField("arrival_proof_json")
    private String arrivalProofJson;
    private String contactName;
    private String contactMobile;
    private String vehicleNo;
    private Long status;

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
}
