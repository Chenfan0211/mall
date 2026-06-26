package com.mall.api.station.vo;

import com.mall.common.page.PageQuery;

public class StationLeaveQueryVO extends PageQuery {

    private String keyword;
    private Long cityId;
    private Long stationId;
    private Long leaderId;
    private Long auditStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

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

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }
}
