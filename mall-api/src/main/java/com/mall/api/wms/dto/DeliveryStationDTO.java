package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class DeliveryStationDTO {

    private Long id;
    private Long deliveryId;
    private Long stationId;
    private Long deliverySort;
    private Long status;
    private LocalDateTime arriveTime;
    private LocalDateTime completeTime;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getDeliverySort() {
        return deliverySort;
    }

    public void setDeliverySort(final Long deliverySort) {
        this.deliverySort = deliverySort;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(final LocalDateTime arriveTime) {
        this.arriveTime = arriveTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(final LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
