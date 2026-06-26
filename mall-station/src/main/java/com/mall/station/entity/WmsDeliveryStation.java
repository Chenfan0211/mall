package com.mall.station.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("wms_delivery_station")
public class WmsDeliveryStation extends BaseEntity {

    private Long deliveryId;
    private Long stationId;
    private Long deliverySort;
    private Long status;
    private LocalDateTime arriveTime;
    private LocalDateTime completeTime;
    private String remark;

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
}
