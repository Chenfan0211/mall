package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class ZoneDTO {

    private Long id;
    private Long warehouseId;
    private String zoneCode;
    private String zoneName;
    private Long zoneType;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
