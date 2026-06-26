package com.mall.api.wms.vo;

import com.mall.common.page.PageQuery;

public class WmsInventoryQueryVO extends PageQuery {

    private String keyword;
    private Long warehouseId;
    private Long zoneId;
    private Long locationId;
    private Long skuId;
    private Long supplierId;
    private Long inventoryStatus;
    private Long lockStatus;
    private Long changeType;
    private Long bizType;
    private String bizNo;
    private Long status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(final Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(final Long locationId) {
        this.locationId = locationId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(final Long inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public Long getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(final Long lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Long getChangeType() {
        return changeType;
    }

    public void setChangeType(final Long changeType) {
        this.changeType = changeType;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
