package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_line_station")
public class WmsLineStation extends BaseEntity {

    private Long lineId;
    private Long stationId;
    private Long deliverySort;
    private Long status;

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
}
