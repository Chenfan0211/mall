package com.mall.api.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserProductCardDTO {

    private Long publishSkuId;
    private Long periodId;
    private Long productId;
    private Long skuId;
    private String productName;
    private String skuName;
    private String mainImageUrl;
    private String saleSpecText;
    private BigDecimal salePrice;
    private Long limitQty;
    private Long minBuyQty;
    private Long plannedStockQty;
    private Long soldQty;
    private Long lockedQty;
    private Long availableQty;
    private Long skuCount;
    private Long availableSkuCount;
    private BigDecimal minSalePrice;
    private BigDecimal maxSalePrice;
    private LocalDate deliveryDate;
    private LocalDateTime saleEndTime;

    public Long getPublishSkuId() {
        return publishSkuId;
    }

    public void setPublishSkuId(final Long publishSkuId) {
        this.publishSkuId = publishSkuId;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(final String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getSaleSpecText() {
        return saleSpecText;
    }

    public void setSaleSpecText(final String saleSpecText) {
        this.saleSpecText = saleSpecText;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
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

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(final Long availableQty) {
        this.availableQty = availableQty;
    }

    public Long getSkuCount() {
        return skuCount;
    }

    public void setSkuCount(final Long skuCount) {
        this.skuCount = skuCount;
    }

    public Long getAvailableSkuCount() {
        return availableSkuCount;
    }

    public void setAvailableSkuCount(final Long availableSkuCount) {
        this.availableSkuCount = availableSkuCount;
    }

    public BigDecimal getMinSalePrice() {
        return minSalePrice;
    }

    public void setMinSalePrice(final BigDecimal minSalePrice) {
        this.minSalePrice = minSalePrice;
    }

    public BigDecimal getMaxSalePrice() {
        return maxSalePrice;
    }

    public void setMaxSalePrice(final BigDecimal maxSalePrice) {
        this.maxSalePrice = maxSalePrice;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocalDateTime getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(final LocalDateTime saleEndTime) {
        this.saleEndTime = saleEndTime;
    }
}
