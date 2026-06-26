package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("ord_cart")
public class OrdCart extends BaseEntity {

    private Long userId;
    private Long cityId;
    private Long stationId;
    private Long periodId;
    private Long productId;
    private Long skuId;
    private Long qty;
    private Long selectedFlag;
    private Long validStatus;

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
}
