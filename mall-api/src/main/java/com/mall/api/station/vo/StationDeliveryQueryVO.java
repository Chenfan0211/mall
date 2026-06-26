package com.mall.api.station.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class StationDeliveryQueryVO extends PageQuery {

    private String keyword;
    private Long stationId;
    private Long deliveryId;
    private Long status;
    private LocalDate deliveryDate;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
