package com.mall.api.user.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UserOrderSubmitVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "城市ID不能为空")
    private Long cityId;

    private Long regionId;

    @NotNull(message = "自提点ID不能为空")
    private Long stationId;

    private Long leaderId;

    @NotBlank(message = "提货人姓名不能为空")
    private String pickupName;

    @NotBlank(message = "提货人手机号不能为空")
    private String pickupMobile;

    @NotBlank(message = "幂等键不能为空")
    private String idempotentKey;

    private List<Long> cartItemIds;

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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(final Long regionId) {
        this.regionId = regionId;
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

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(final String pickupName) {
        this.pickupName = pickupName;
    }

    public String getPickupMobile() {
        return pickupMobile;
    }

    public void setPickupMobile(final String pickupMobile) {
        this.pickupMobile = pickupMobile;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(final String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }

    public List<Long> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(final List<Long> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }
}
