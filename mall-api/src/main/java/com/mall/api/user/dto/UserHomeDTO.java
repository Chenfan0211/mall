package com.mall.api.user.dto;

public class UserHomeDTO {

    private Long userId;
    private Long cityId;
    private Long stationId;
    private Long onlineProductCount;
    private Long cartItemCount;
    private Long waitPayOrderCount;
    private Long waitPickupOrderCount;
    private Long unreadMessageCount;

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

    public Long getOnlineProductCount() {
        return onlineProductCount;
    }

    public void setOnlineProductCount(final Long onlineProductCount) {
        this.onlineProductCount = onlineProductCount;
    }

    public Long getCartItemCount() {
        return cartItemCount;
    }

    public void setCartItemCount(final Long cartItemCount) {
        this.cartItemCount = cartItemCount;
    }

    public Long getWaitPayOrderCount() {
        return waitPayOrderCount;
    }

    public void setWaitPayOrderCount(final Long waitPayOrderCount) {
        this.waitPayOrderCount = waitPayOrderCount;
    }

    public Long getWaitPickupOrderCount() {
        return waitPickupOrderCount;
    }

    public void setWaitPickupOrderCount(final Long waitPickupOrderCount) {
        this.waitPickupOrderCount = waitPickupOrderCount;
    }

    public Long getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(final Long unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }
}
