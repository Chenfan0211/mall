package com.mall.api.user.vo;

import com.mall.common.page.PageQuery;
import jakarta.validation.constraints.NotNull;

public class UserAfterSaleQueryVO extends PageQuery {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String keyword;
    private Long afterSaleType;
    private Long status;

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
}
