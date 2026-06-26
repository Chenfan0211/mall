package com.mall.api.user.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UserCartAddVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "城市ID不能为空")
    private Long cityId;

    @NotNull(message = "自提点ID不能为空")
    private Long stationId;

    @NotNull(message = "团期ID不能为空")
    private Long periodId;

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "SKUID不能为空")
    private Long skuId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Long qty;

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

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(final Long periodId) {
        this.periodId = periodId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(final Long qty) {
        this.qty = qty;
    }
}
