package com.mall.api.aftersale.vo;

import com.mall.common.page.PageQuery;

public class AfterSaleReturnRecordQueryVO extends PageQuery {

    private String keyword;
    private Long afterSaleId;
    private Long orderItemId;
    private Long skuId;
    private Long stationId;
    private Long warehouseId;
    private Long returnStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(final Long afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(final Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(final Long returnStatus) {
        this.returnStatus = returnStatus;
    }
}
