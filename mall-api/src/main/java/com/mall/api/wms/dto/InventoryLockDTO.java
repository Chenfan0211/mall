package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class InventoryLockDTO {

    private Long id;
    private String lockNo;
    private Long warehouseId;
    private Long inventoryId;
    private Long skuId;
    private Long bizType;
    private String bizNo;
    private Long lockQty;
    private Long releaseQty;
    private Long lockStatus;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLockNo() {
        return lockNo;
    }

    public void setLockNo(final String lockNo) {
        this.lockNo = lockNo;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(final Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getBizType() {
        return bizType;
    }

    public void setBizType(final Long bizType) {
        this.bizType = bizType;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(final String bizNo) {
        this.bizNo = bizNo;
    }

    public Long getLockQty() {
        return lockQty;
    }

    public void setLockQty(final Long lockQty) {
        this.lockQty = lockQty;
    }

    public Long getReleaseQty() {
        return releaseQty;
    }

    public void setReleaseQty(final Long releaseQty) {
        this.releaseQty = releaseQty;
    }

    public Long getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(final Long lockStatus) {
        this.lockStatus = lockStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
