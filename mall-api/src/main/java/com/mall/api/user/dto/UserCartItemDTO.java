package com.mall.api.user.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserCartItemDTO {

    private Long id;
    private Long userId;
    private Long cityId;
    private Long stationId;
    private Long periodId;
    private Long productId;
    private Long skuId;
    private String productName;
    private String skuName;
    private String mainImageUrl;
    private BigDecimal salePrice;
    private Long qty;
    private Long selectedFlag;
    private Long validStatus;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
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

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(final Long qty) {
        this.qty = qty;
    }

    public Long getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(final Long selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public Long getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(final Long validStatus) {
        this.validStatus = validStatus;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
