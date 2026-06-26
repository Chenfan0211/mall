package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class LoadingItemDTO {

    private Long id;
    private Long loadingId;
    private Long stationId;
    private Long skuId;
    private Long requiredQty;
    private Long actualQty;
    private Long outboundQty;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getLoadingId() {
        return loadingId;
    }

    public void setLoadingId(final Long loadingId) {
        this.loadingId = loadingId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
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

    public Long getOutboundQty() {
        return outboundQty;
    }

    public void setOutboundQty(final Long outboundQty) {
        this.outboundQty = outboundQty;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
