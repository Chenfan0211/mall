package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_pick_item")
public class WmsPickItem extends BaseEntity {

    private Long pickId;
    private Long skuId;
    private Long inventoryId;
    private Long locationId;
    private Long requiredQty;
    private Long actualQty;
    private Long shortageQty;
    private String diffReason;

    public Long getPickId() {
        return pickId;
    }

    public void setPickId(final Long pickId) {
        this.pickId = pickId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(final Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(final Long locationId) {
        this.locationId = locationId;
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

    public Long getShortageQty() {
        return shortageQty;
    }

    public void setShortageQty(final Long shortageQty) {
        this.shortageQty = shortageQty;
    }

    public String getDiffReason() {
        return diffReason;
    }

    public void setDiffReason(final String diffReason) {
        this.diffReason = diffReason;
    }
}
