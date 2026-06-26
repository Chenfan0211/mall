package com.mall.api.user.vo;

import com.mall.common.page.PageQuery;

public class UserStationQueryVO extends PageQuery {

    private String keyword;
    private Long cityId;
    private Long status;

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

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
