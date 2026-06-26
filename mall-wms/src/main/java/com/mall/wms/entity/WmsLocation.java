package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_location")
public class WmsLocation extends BaseEntity {

    private Long warehouseId;
    private Long zoneId;
    private String locationCode;
    private String locationName;
    private Long capacityQty;
    private Long status;

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
}
