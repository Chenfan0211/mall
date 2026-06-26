package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_zone")
public class WmsZone extends BaseEntity {

    private Long warehouseId;
    private String zoneCode;
    private String zoneName;
    private Long zoneType;
    private Long status;

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(final String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(final String zoneName) {
        this.zoneName = zoneName;
    }

    public Long getZoneType() {
        return zoneType;
    }

    public void setZoneType(final Long zoneType) {
        this.zoneType = zoneType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
