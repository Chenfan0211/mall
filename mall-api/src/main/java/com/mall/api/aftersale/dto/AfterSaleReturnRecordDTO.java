package com.mall.api.aftersale.dto;

import java.time.LocalDateTime;

public class AfterSaleReturnRecordDTO {

    private Long id;
    private String returnNo;
    private Long afterSaleId;
    private Long orderItemId;
    private Long skuId;
    private Long stationId;
    private Long warehouseId;
    private Long returnQty;
    private Long returnStatus;
    private String proofFileJson;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getReturnNo() {
        return returnNo;
    }

    public void setReturnNo(final String returnNo) {
        this.returnNo = returnNo;
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

    public Long getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(final Long returnQty) {
        this.returnQty = returnQty;
    }

    public Long getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(final Long returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getProofFileJson() {
        return proofFileJson;
    }

    public void setProofFileJson(final String proofFileJson) {
        this.proofFileJson = proofFileJson;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
