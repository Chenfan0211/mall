package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class WarehouseDTO {

    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private Long cityId;
    private Long warehouseType;
    private Long managerAccountId;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(final String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(final String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(final Long warehouseType) {
        this.warehouseType = warehouseType;
    }

    public Long getManagerAccountId() {
        return managerAccountId;
    }

    public void setManagerAccountId(final Long managerAccountId) {
        this.managerAccountId = managerAccountId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
