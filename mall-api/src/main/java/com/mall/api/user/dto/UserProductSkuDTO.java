package com.mall.api.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserProductSkuDTO {

    private Long publishSkuId;
    private Long periodId;
    private Long skuId;
    private String skuName;
    private String saleSpecText;
    private String baseUnit;
    private BigDecimal salePrice;
    private Long limitQty;
    private Long minBuyQty;
    private Long availableQty;
    private LocalDate deliveryDate;

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

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    public String getSaleSpecText() {
        return saleSpecText;
    }

    public void setSaleSpecText(final String saleSpecText) {
        this.saleSpecText = saleSpecText;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(final String baseUnit) {
        this.baseUnit = baseUnit;
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

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(final Long availableQty) {
        this.availableQty = availableQty;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
