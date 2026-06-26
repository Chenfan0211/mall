package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("wms_pick_task")
public class WmsPickTask extends BaseEntity {

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
}
