package com.mall.api.operation.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ExceptionRecordDTO implements Serializable {

    private Long id;
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
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
