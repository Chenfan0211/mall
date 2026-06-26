package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class PutawayTaskDTO {

    private Long id;
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
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
