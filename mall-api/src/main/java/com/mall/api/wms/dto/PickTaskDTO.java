package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class PickTaskDTO {

    private Long id;
    private String pickNo;
    private Long waveId;
    private Long warehouseId;
    private Long lineId;
    private Long assignAccountId;
    private Long requiredQty;
    private Long actualQty;
    private Long shortageQty;
    private Long status;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getPickNo() {
        return pickNo;
    }

    public void setPickNo(final String pickNo) {
        this.pickNo = pickNo;
    }

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(final Long waveId) {
        this.waveId = waveId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(final Long lineId) {
        this.lineId = lineId;
    }

    public Long getAssignAccountId() {
        return assignAccountId;
    }

    public void setAssignAccountId(final Long assignAccountId) {
        this.assignAccountId = assignAccountId;
    }

    public Long getRequiredQty() {
        return requiredQty;
    }

    public void setRequiredQty(final Long requiredQty) {
        this.requiredQty = requiredQty;
    }

    public Long getActualQty() {
        return actualQty;
    }

    public void setActualQty(final Long actualQty) {
        this.actualQty = actualQty;
    }

    public Long getShortageQty() {
        return shortageQty;
    }

    public void setShortageQty(final Long shortageQty) {
        this.shortageQty = shortageQty;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(final LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
