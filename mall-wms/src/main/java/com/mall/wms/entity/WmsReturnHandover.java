package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("wms_return_handover")
public class WmsReturnHandover extends BaseEntity {

    private String returnNo;
    private Long deliveryId;
    private Long stationId;
    private Long skuId;
    private Long warehouseId;
    private Long returnQty;
    private Long reasonType;
    private Long driverAccountId;
    private Long supervisorAccountId;
    private Long status;
    private LocalDateTime confirmTime;
    private String remark;

    public String getReturnNo() {
        return returnNo;
    }

    public void setReturnNo(final String returnNo) {
        this.returnNo = returnNo;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
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

    public Long getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(final Long returnQty) {
        this.returnQty = returnQty;
    }

    public Long getReasonType() {
        return reasonType;
    }

    public void setReasonType(final Long reasonType) {
        this.reasonType = reasonType;
    }

    public Long getDriverAccountId() {
        return driverAccountId;
    }

    public void setDriverAccountId(final Long driverAccountId) {
        this.driverAccountId = driverAccountId;
    }

    public Long getSupervisorAccountId() {
        return supervisorAccountId;
    }

    public void setSupervisorAccountId(final Long supervisorAccountId) {
        this.supervisorAccountId = supervisorAccountId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(final LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }
}
