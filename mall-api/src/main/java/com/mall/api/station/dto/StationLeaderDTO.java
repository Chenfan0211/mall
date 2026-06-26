package com.mall.api.station.dto;

import java.time.LocalDateTime;

public class StationLeaderDTO {

    private Long id;
    private Long stationId;
    private Long leaderId;
    private Long bindStatus;
    private LocalDateTime bindTime;
    private LocalDateTime unbindTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public Long getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(final Long bindStatus) {
        this.bindStatus = bindStatus;
    }

    public LocalDateTime getBindTime() {
        return bindTime;
    }

    public void setBindTime(final LocalDateTime bindTime) {
        this.bindTime = bindTime;
    }

    public LocalDateTime getUnbindTime() {
        return unbindTime;
    }

    public void setUnbindTime(final LocalDateTime unbindTime) {
        this.unbindTime = unbindTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
