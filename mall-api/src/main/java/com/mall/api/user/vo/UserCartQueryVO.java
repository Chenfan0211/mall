package com.mall.api.user.vo;

import com.mall.common.page.PageQuery;
import jakarta.validation.constraints.NotNull;

public class UserCartQueryVO extends PageQuery {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long cityId;
    private Long stationId;
    private Long selectedFlag;
    private Long validStatus;

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

    public Long getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(final Long selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public Long getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(final Long validStatus) {
        this.validStatus = validStatus;
    }
}
