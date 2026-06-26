package com.mall.api.wms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryDTO {

    private Long id;
    private Long warehouseId;
    private Long zoneId;
    private Long locationId;
    private Long skuId;
    private Long supplierId;
    private String batchNo;
    private LocalDate productionDate;
    private Long shelfLifeDays;
    private Long inStockQty;
    private Long lockedQty;
    private Long availableQty;
    private Long frozenQty;
    private Long inventoryStatus;
    private Long version;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(final String batchNo) {
        this.batchNo = batchNo;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(final LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public Long getShelfLifeDays() {
        return shelfLifeDays;
    }

    public void setShelfLifeDays(final Long shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

    public Long getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(final Long inStockQty) {
        this.inStockQty = inStockQty;
    }

    public Long getLockedQty() {
        return lockedQty;
    }

    public void setLockedQty(final Long lockedQty) {
        this.lockedQty = lockedQty;
    }

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(final Long availableQty) {
        this.availableQty = availableQty;
    }

    public Long getFrozenQty() {
        return frozenQty;
    }

    public void setFrozenQty(final Long frozenQty) {
        this.frozenQty = frozenQty;
    }

    public Long getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(final Long inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
