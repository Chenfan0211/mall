package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class InventoryLogDTO {

    private Long id;
    private String logNo;
    private Long warehouseId;
    private Long zoneId;
    private Long locationId;
    private Long skuId;
    private String batchNo;
    private Long changeType;
    private Long beforeQty;
    private Long changeQty;
    private Long afterQty;
    private String bizType;
    private String bizNo;
    private Long operatorAccountId;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLogNo() {
        return logNo;
    }

    public void setLogNo(final String logNo) {
        this.logNo = logNo;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(final Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(final Long locationId) {
        this.locationId = locationId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(final String batchNo) {
        this.batchNo = batchNo;
    }

    public Long getChangeType() {
        return changeType;
    }

    public void setChangeType(final Long changeType) {
        this.changeType = changeType;
    }

    public Long getBeforeQty() {
        return beforeQty;
    }

    public void setBeforeQty(final Long beforeQty) {
        this.beforeQty = beforeQty;
    }

    public Long getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(final Long changeQty) {
        this.changeQty = changeQty;
    }

    public Long getAfterQty() {
        return afterQty;
    }

    public void setAfterQty(final Long afterQty) {
        this.afterQty = afterQty;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(final String bizType) {
        this.bizType = bizType;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(final String bizNo) {
        this.bizNo = bizNo;
    }

    public Long getOperatorAccountId() {
        return operatorAccountId;
    }

    public void setOperatorAccountId(final Long operatorAccountId) {
        this.operatorAccountId = operatorAccountId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
