package com.mall.api.user.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UserAfterSaleApplyVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "订单商品ID不能为空")
    private Long orderItemId;

    @NotNull(message = "售后类型不能为空")
    private Long afterSaleType;

    @NotNull(message = "申请数量不能为空")
    @Min(value = 1, message = "申请数量必须大于0")
    private Long applyQty;

    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "退款金额必须大于0")
    private BigDecimal refundAmount;

    @NotBlank(message = "申请原因不能为空")
    private String applyReason;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(final Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getAfterSaleType() {
        return afterSaleType;
    }

    public void setAfterSaleType(final Long afterSaleType) {
        this.afterSaleType = afterSaleType;
    }

    public Long getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(final Long applyQty) {
        this.applyQty = applyQty;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(final BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(final String applyReason) {
        this.applyReason = applyReason;
    }
}
