package com.mall.api.station.dto;

import java.time.LocalDateTime;

public class LeaderDTO {

    private Long id;
    private String leaderNo;
    private Long userId;
    private String leaderName;
    private String leaderMobile;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLeaderNo() {
        return leaderNo;
    }

    public void setLeaderNo(final String leaderNo) {
        this.leaderNo = leaderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(final String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderMobile() {
        return leaderMobile;
    }

    public void setLeaderMobile(final String leaderMobile) {
        this.leaderMobile = leaderMobile;
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
