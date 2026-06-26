package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class LocationDTO {

    private Long id;
    private Long warehouseId;
    private Long zoneId;
    private String locationCode;
    private String locationName;
    private Long capacityQty;
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

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(final Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(final String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(final String locationName) {
        this.locationName = locationName;
    }

    public Long getCapacityQty() {
        return capacityQty;
    }

    public void setCapacityQty(final Long capacityQty) {
        this.capacityQty = capacityQty;
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
