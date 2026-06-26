package com.mall.api.user.vo;

import jakarta.validation.constraints.NotNull;

public class UserHomeQueryVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long cityId;
    private Long stationId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
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
}
