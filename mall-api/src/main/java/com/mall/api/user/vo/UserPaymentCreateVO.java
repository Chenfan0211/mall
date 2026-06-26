package com.mall.api.user.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserPaymentCreateVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "支付渠道不能为空")
    private Long channelType;

    @NotBlank(message = "幂等键不能为空")
    private String idempotentKey;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getChannelType() {
        return channelType;
    }

    public void setChannelType(final Long channelType) {
        this.channelType = channelType;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(final String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }
}
