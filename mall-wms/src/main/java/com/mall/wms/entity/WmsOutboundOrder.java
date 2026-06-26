package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("wms_outbound_order")
public class WmsOutboundOrder extends BaseEntity {

    private String outboundNo;
    private Long loadingId;
    private Long warehouseId;
    private Long lineId;
    private Long outboundQty;
    private Long status;
    private Long outboundAccountId;
    private LocalDateTime outboundTime;
    private String idempotentKey;

    public String getOutboundNo() {
        return outboundNo;
    }

    public void setOutboundNo(final String outboundNo) {
        this.outboundNo = outboundNo;
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

    public Long getOutboundQty() {
        return outboundQty;
    }

    public void setOutboundQty(final Long outboundQty) {
        this.outboundQty = outboundQty;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
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

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(final String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }
}
