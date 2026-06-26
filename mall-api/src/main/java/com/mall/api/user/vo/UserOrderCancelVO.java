package com.mall.api.user.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserOrderCancelVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(final String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
