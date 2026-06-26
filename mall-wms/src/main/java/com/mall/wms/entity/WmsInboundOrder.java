package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("wms_inbound_order")
public class WmsInboundOrder extends BaseEntity {

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
}
