package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("ord_order_item")
public class OrdOrderItem extends BaseEntity {

    private Long orderId;
    private String orderNo;
    private String orderItemNo;
    private Long periodId;
    private Long productId;
    private Long skuId;
    private Long supplierId;
    private Long warehouseId;
    private Long cityId;
    private Long stationId;
    private String productName;
    private String skuName;
    private BigDecimal salePrice;
    private Long qty;
    private Long pickedQty;
    private Long refundedQty;
    private BigDecimal itemAmount;
    private Long fulfillStatus;
    private Long afterSaleStatus;
    private LocalDate expectedPickupDate;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderItemNo() {
        return orderItemNo;
    }

    public void setOrderItemNo(final String orderItemNo) {
        this.orderItemNo = orderItemNo;
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

    public Long getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(final Long pickedQty) {
        this.pickedQty = pickedQty;
    }

    public Long getRefundedQty() {
        return refundedQty;
    }

    public void setRefundedQty(final Long refundedQty) {
        this.refundedQty = refundedQty;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(final BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
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

    public LocalDate getExpectedPickupDate() {
        return expectedPickupDate;
    }

    public void setExpectedPickupDate(final LocalDate expectedPickupDate) {
        this.expectedPickupDate = expectedPickupDate;
    }
}
