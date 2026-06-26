package com.mall.api.aftersale.vo;

import com.mall.common.page.PageQuery;

public class AfterSaleQueryVO extends PageQuery {

    private String keyword;
    private Long orderId;
    private String orderNo;
    private Long userId;
    private Long cityId;
    private Long stationId;
    private Long afterSaleType;
    private Long status;
    private Long responsibilityType;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

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

    public Long getAfterSaleType() {
        return afterSaleType;
    }

    public void setAfterSaleType(final Long afterSaleType) {
        this.afterSaleType = afterSaleType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(final Long responsibilityType) {
        this.responsibilityType = responsibilityType;
    }
}
