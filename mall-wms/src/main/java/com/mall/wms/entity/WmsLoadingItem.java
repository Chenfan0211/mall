package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_loading_item")
public class WmsLoadingItem extends BaseEntity {

    private Long loadingId;
    private Long stationId;
    private Long skuId;
    private Long requiredQty;
    private Long actualQty;
    private Long outboundQty;

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
}
