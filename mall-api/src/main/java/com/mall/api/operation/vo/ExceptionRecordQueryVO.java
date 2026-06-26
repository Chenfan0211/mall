package com.mall.api.operation.vo;

import com.mall.common.page.PageQuery;

public class ExceptionRecordQueryVO extends PageQuery {

    private Long exceptionType;
    private Long priority;
    private Long status;
    private String sourceModule;
    private String sourceBizType;
    private Long cityId;
    private Long supplierId;
    private Long stationId;
    private Long warehouseId;
    private Long ownerAccountId;

    public Long getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(final Long exceptionType) {
        this.exceptionType = exceptionType;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(final Long priority) {
        this.priority = priority;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public String getSourceModule() {
        return sourceModule;
    }

    public void setSourceModule(final String sourceModule) {
        this.sourceModule = sourceModule;
    }

    public String getSourceBizType() {
        return sourceBizType;
    }

    public void setSourceBizType(final String sourceBizType) {
        this.sourceBizType = sourceBizType;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
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

    public Long getOwnerAccountId() {
        return ownerAccountId;
    }

    public void setOwnerAccountId(final Long ownerAccountId) {
        this.ownerAccountId = ownerAccountId;
    }
}
