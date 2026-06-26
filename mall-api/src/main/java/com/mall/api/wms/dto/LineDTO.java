package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class LineDTO {

    private Long id;
    private Long warehouseId;
    private String lineCode;
    private String lineName;
    private Long defaultDriverAccountId;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
