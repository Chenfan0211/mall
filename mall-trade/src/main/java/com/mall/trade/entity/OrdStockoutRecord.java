package com.mall.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("ord_stockout_record")
public class OrdStockoutRecord extends BaseEntity {

    private String stockoutNo;
    private Long periodId;
    private Long orderId;
    private Long orderItemId;
    private Long skuId;
    private Long warehouseId;
    private Long stationId;
    private Long shortageQty;
    private Long handledQty;
    private String reason;
    private Long status;

    public String getStockoutNo() {
        return stockoutNo;
    }

    public void setStockoutNo(final String stockoutNo) {
        this.stockoutNo = stockoutNo;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(final Long periodId) {
        this.periodId = periodId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getShortageQty() {
        return shortageQty;
    }

    public void setShortageQty(final Long shortageQty) {
        this.shortageQty = shortageQty;
    }

    public Long getHandledQty() {
        return handledQty;
    }

    public void setHandledQty(final Long handledQty) {
        this.handledQty = handledQty;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
