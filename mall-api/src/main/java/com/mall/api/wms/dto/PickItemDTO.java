package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class PickItemDTO {

    private Long id;
    private Long pickId;
    private Long skuId;
    private Long inventoryId;
    private Long locationId;
    private Long requiredQty;
    private Long actualQty;
    private Long shortageQty;
    private String diffReason;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
