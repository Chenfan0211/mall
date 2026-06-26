package com.mall.api.station.vo;

import com.mall.common.page.PageQuery;

public class LeaderQueryVO extends PageQuery {

    private String keyword;
    private Long leaderId;
    private Long stationId;
    private Long status;
    private Long bindStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(final Long leaderId) {
        this.leaderId = leaderId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(final Long bindStatus) {
        this.bindStatus = bindStatus;
    }
}
