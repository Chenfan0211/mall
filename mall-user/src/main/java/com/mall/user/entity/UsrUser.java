package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("usr_user")
public class UsrUser extends BaseEntity {

    private String userNo;
    private String openId;
    private String unionId;
    private String nickname;
    private String avatarUrl;
    private String mobile;
    private Long defaultCityId;
    private Long defaultStationId;
    private Long riskLevel;
    private Long status;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(final String userNo) {
        this.userNo = userNo;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(final String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(final String unionId) {
        this.unionId = unionId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public Long getDefaultCityId() {
        return defaultCityId;
    }

    public void setDefaultCityId(final Long defaultCityId) {
        this.defaultCityId = defaultCityId;
    }

    public Long getDefaultStationId() {
        return defaultStationId;
    }

    public void setDefaultStationId(final Long defaultStationId) {
        this.defaultStationId = defaultStationId;
    }

    public Long getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(final Long riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
