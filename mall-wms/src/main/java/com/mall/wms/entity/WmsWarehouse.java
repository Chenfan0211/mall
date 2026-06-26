package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_warehouse")
public class WmsWarehouse extends BaseEntity {

    private String warehouseCode;
    private String warehouseName;
    private Long cityId;
    private Long warehouseType;
    private Long managerAccountId;
    private Long status;

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
}
