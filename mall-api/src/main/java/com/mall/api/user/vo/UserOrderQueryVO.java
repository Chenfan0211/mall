package com.mall.api.user.vo;

import com.mall.common.page.PageQuery;
import jakarta.validation.constraints.NotNull;

public class UserOrderQueryVO extends PageQuery {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String keyword;
    private Long tradeStatus;
    private Long payStatus;
    private Long fulfillStatus;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(final Long tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Long getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(final Long payStatus) {
        this.payStatus = payStatus;
    }

    public Long getFulfillStatus() {
        return fulfillStatus;
    }

    public void setFulfillStatus(final Long fulfillStatus) {
        this.fulfillStatus = fulfillStatus;
    }
}
