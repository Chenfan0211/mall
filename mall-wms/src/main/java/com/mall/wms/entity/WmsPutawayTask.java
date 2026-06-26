package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("wms_putaway_task")
public class WmsPutawayTask extends BaseEntity {

    private String putawayNo;
    private Long inboundId;
    private Long skuId;
    private Long warehouseId;
    private Long fromZoneId;
    private Long targetZoneId;
    private Long targetLocationId;
    private Long putawayQty;
    private Long actualQty;
    private Long status;
    private Long assignAccountId;
    private LocalDateTime completeTime;

    public String getPutawayNo() {
        return putawayNo;
    }

    public void setPutawayNo(final String putawayNo) {
        this.putawayNo = putawayNo;
    }

    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(final Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getFromZoneId() {
        return fromZoneId;
    }

    public void setFromZoneId(final Long fromZoneId) {
        this.fromZoneId = fromZoneId;
    }

    public Long getTargetZoneId() {
        return targetZoneId;
    }

    public void setTargetZoneId(final Long targetZoneId) {
        this.targetZoneId = targetZoneId;
    }

    public Long getTargetLocationId() {
        return targetLocationId;
    }

    public void setTargetLocationId(final Long targetLocationId) {
        this.targetLocationId = targetLocationId;
    }

    public Long getPutawayQty() {
        return putawayQty;
    }

    public void setPutawayQty(final Long putawayQty) {
        this.putawayQty = putawayQty;
    }

    public Long getActualQty() {
        return actualQty;
    }

    public void setActualQty(final Long actualQty) {
        this.actualQty = actualQty;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getAssignAccountId() {
        return assignAccountId;
    }

    public void setAssignAccountId(final Long assignAccountId) {
        this.assignAccountId = assignAccountId;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(final LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }
}
