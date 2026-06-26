package com.mall.job.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("op_exception_record")
public class JobExceptionRecord extends BaseEntity {

    private String exceptionNo;
    private Long exceptionType;
    private Long priority;
    private String sourceModule;
    private String sourceBizType;
    private String sourceBizNo;
    private Long cityId;
    private Long supplierId;
    private Long stationId;
    private Long warehouseId;
    private Long ownerAccountId;
    private Long status;
    private String title;
    private String description;

    public String getExceptionNo() {
        return exceptionNo;
    }

    public void setExceptionNo(final String exceptionNo) {
        this.exceptionNo = exceptionNo;
    }

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

    public String getSourceBizNo() {
        return sourceBizNo;
    }

    public void setSourceBizNo(final String sourceBizNo) {
        this.sourceBizNo = sourceBizNo;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
