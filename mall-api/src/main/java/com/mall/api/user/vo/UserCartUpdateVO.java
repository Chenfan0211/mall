package com.mall.api.user.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UserCartUpdateVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Min(value = 1, message = "数量必须大于0")
    private Long qty;

    private Long selectedFlag;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(final Long qty) {
        this.qty = qty;
    }

    public Long getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(final Long selectedFlag) {
        this.selectedFlag = selectedFlag;
    }
}
