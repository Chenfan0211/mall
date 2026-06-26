package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("afs_return_record")
public class AfsReturnRecord extends BaseEntity {

    private String returnNo;
    private Long afterSaleId;
    private Long orderItemId;
    private Long skuId;
    private Long stationId;
    private Long warehouseId;
    private Long returnQty;
    private Long returnStatus;

    @TableField("proof_file_json")
    private String proofFileJson;

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
}
