package com.mall.station.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("usr_station_leader")
public class UsrStationLeader extends BaseEntity {

    private Long stationId;
    private Long leaderId;
    private Long bindStatus;
    private LocalDateTime bindTime;
    private LocalDateTime unbindTime;

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
}
