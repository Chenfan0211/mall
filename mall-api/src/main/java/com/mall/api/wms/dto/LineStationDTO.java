package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class LineStationDTO {

    private Long id;
    private Long lineId;
    private Long stationId;
    private Long deliverySort;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(final Long lineId) {
        this.lineId = lineId;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
