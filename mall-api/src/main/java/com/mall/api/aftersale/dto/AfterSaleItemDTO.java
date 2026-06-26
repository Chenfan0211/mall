package com.mall.api.aftersale.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AfterSaleItemDTO {

    private Long id;
    private Long afterSaleId;
    private Long orderItemId;
    private Long skuId;
    private Long supplierId;
    private Long applyQty;
    private Long approvedQty;
    private BigDecimal refundAmount;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(final Long afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(final Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(final Long applyQty) {
        this.applyQty = applyQty;
    }

    public Long getApprovedQty() {
        return approvedQty;
    }

    public void setApprovedQty(final Long approvedQty) {
        this.approvedQty = approvedQty;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(final BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
