package com.mall.api.trade.vo;

import com.mall.common.page.PageQuery;

public class OrderItemQueryVO extends PageQuery {

    private Long orderId;
    private Long periodId;
    private Long productId;
    private Long skuId;
    private Long supplierId;
    private Long warehouseId;
    private Long cityId;
    private Long stationId;
    private Long fulfillStatus;
    private Long afterSaleStatus;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
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

    public Long getFulfillStatus() {
        return fulfillStatus;
    }

    public void setFulfillStatus(final Long fulfillStatus) {
        this.fulfillStatus = fulfillStatus;
    }

    public Long getAfterSaleStatus() {
        return afterSaleStatus;
    }

    public void setAfterSaleStatus(final Long afterSaleStatus) {
        this.afterSaleStatus = afterSaleStatus;
    }
}
