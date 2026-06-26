package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class InboundOrderDTO {

    private Long id;
    private String inboundNo;
    private Long sourceType;
    private String sourceNo;
    private Long purchaseId;
    private Long deliveryId;
    private Long supplierId;
    private Long warehouseId;
    private Long status;
    private Long receiveAccountId;
    private LocalDateTime receiveTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getInboundNo() {
        return inboundNo;
    }

    public void setInboundNo(final String inboundNo) {
        this.inboundNo = inboundNo;
    }

    public Long getSourceType() {
        return sourceType;
    }

    public void setSourceType(final Long sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(final String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(final Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final Long deliveryId) {
        this.deliveryId = deliveryId;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getReceiveAccountId() {
        return receiveAccountId;
    }

    public void setReceiveAccountId(final Long receiveAccountId) {
        this.receiveAccountId = receiveAccountId;
    }

    public LocalDateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(final LocalDateTime receiveTime) {
        this.receiveTime = receiveTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
