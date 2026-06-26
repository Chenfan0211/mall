package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_line")
public class WmsLine extends BaseEntity {

    private Long warehouseId;
    private String lineCode;
    private String lineName;
    private Long defaultDriverAccountId;
    private Long status;

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(final String lineCode) {
        this.lineCode = lineCode;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(final String lineName) {
        this.lineName = lineName;
    }

    public Long getDefaultDriverAccountId() {
        return defaultDriverAccountId;
    }

    public void setDefaultDriverAccountId(final Long defaultDriverAccountId) {
        this.defaultDriverAccountId = defaultDriverAccountId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
