package com.mall.api.station.vo;

import java.time.LocalDate;

public class StationWorkbenchQueryVO {

    private Long stationId;
    private Long leaderId;
    private LocalDate bizDate;

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(final Long leaderId) {
        this.leaderId = leaderId;
    }

    public LocalDate getBizDate() {
        return bizDate;
    }

    public void setBizDate(final LocalDate bizDate) {
        this.bizDate = bizDate;
    }
}
