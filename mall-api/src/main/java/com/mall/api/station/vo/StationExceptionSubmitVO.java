package com.mall.api.station.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class StationExceptionSubmitVO {

    private Long stationId;
    private Long leaderId;
    private Long productId;
    private Long skuId;
    private String productName;
    private String skuName;
    private String exceptionType;

    @Min(value = 1, message = "异常总数必须大于0")
    private Long totalQty;

    @Valid
    @NotEmpty(message = "异常明细不能为空")
    private List<Item> items = new ArrayList<>();

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(final Long leaderId) {
        this.leaderId = leaderId;
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

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(final String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public Long getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(final Long totalQty) {
        this.totalQty = totalQty;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(final List<Item> items) {
        this.items = items;
    }

    public static class Item {

        private Long orderItemId;
        private Long orderId;
        private String orderNo;

        @Min(value = 1, message = "异常数量必须大于0")
        private Long qty;

        public Long getOrderItemId() {
            return orderItemId;
        }

        public void setOrderItemId(final Long orderItemId) {
            this.orderItemId = orderItemId;
        }

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

        public Long getQty() {
            return qty;
        }

        public void setQty(final Long qty) {
            this.qty = qty;
        }
    }
}
