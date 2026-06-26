package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("usr_browse_history")
public class UsrBrowseHistory extends BaseEntity {

    private Long userId;
    private Long productId;
    private Long skuId;
    private Long stationId;
    private LocalDateTime browseTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
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

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getBrowseTime() {
        return browseTime;
    }

    public void setBrowseTime(final LocalDateTime browseTime) {
        this.browseTime = browseTime;
    }
}
