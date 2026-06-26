package com.mall.api.sale.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PublishSkuDTO {

    private Long id;
    private Long periodId;
    private Long productId;
    private Long skuId;
    private Long supplierId;
    private BigDecimal salePrice;
    private BigDecimal purchasePrice;
    private Long limitQty;
    private Long minBuyQty;
    private Long plannedStockQty;
    private Long soldQty;
    private Long lockedQty;
    private LocalDate productionDate;
    private Long shelfLifeDays;
    private Long status;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(final Long periodId) {
        this.periodId = periodId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
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

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(final BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getLimitQty() {
        return limitQty;
    }

    public void setLimitQty(final Long limitQty) {
        this.limitQty = limitQty;
    }

    public Long getMinBuyQty() {
        return minBuyQty;
    }

    public void setMinBuyQty(final Long minBuyQty) {
        this.minBuyQty = minBuyQty;
    }

    public Long getPlannedStockQty() {
        return plannedStockQty;
    }

    public void setPlannedStockQty(final Long plannedStockQty) {
        this.plannedStockQty = plannedStockQty;
    }

    public Long getSoldQty() {
        return soldQty;
    }

    public void setSoldQty(final Long soldQty) {
        this.soldQty = soldQty;
    }

    public Long getLockedQty() {
        return lockedQty;
    }

    public void setLockedQty(final Long lockedQty) {
        this.lockedQty = lockedQty;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
