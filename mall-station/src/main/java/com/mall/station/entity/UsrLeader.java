package com.mall.station.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("usr_leader")
public class UsrLeader extends BaseEntity {

    private String leaderNo;
    private Long userId;
    private String leaderName;
    private String leaderMobile;
    private Long status;

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
}
