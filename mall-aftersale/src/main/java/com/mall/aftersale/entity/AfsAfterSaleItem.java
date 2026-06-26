package com.mall.aftersale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;

@TableName("afs_after_sale_item")
public class AfsAfterSaleItem extends BaseEntity {

    private Long afterSaleId;
    private Long orderItemId;
    private Long skuId;
    private Long supplierId;
    private Long applyQty;
    private Long approvedQty;
    private BigDecimal refundAmount;

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
}
