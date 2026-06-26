package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class OutboundOrderDTO {

    private Long id;
    private String outboundNo;
    private Long loadingId;
    private Long warehouseId;
    private Long lineId;
    private Long outboundQty;
    private Long status;
    private Long outboundAccountId;
    private LocalDateTime outboundTime;
    private String idempotentKey;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
