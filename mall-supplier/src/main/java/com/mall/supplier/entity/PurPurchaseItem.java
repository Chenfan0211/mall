package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;

@TableName("pur_purchase_item")
public class PurPurchaseItem extends BaseEntity {

    private Long purchaseId;
    private String purchaseNo;
    private Long productId;
    private Long skuId;
    private Long supplierId;
    private Long warehouseId;
    private Long applyQty;
    private Long purchaseQty;
    private Long receivedQty;
    private Long rejectedQty;
    private BigDecimal purchasePrice;
    private Long status;

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(final Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseNo() {
        return purchaseNo;
    }

    public void setPurchaseNo(final String purchaseNo) {
        this.purchaseNo = purchaseNo;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(final Long applyQty) {
        this.applyQty = applyQty;
    }

    public Long getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(final Long purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public Long getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(final Long receivedQty) {
        this.receivedQty = receivedQty;
    }

    public Long getRejectedQty() {
        return rejectedQty;
    }

    public void setRejectedQty(final Long rejectedQty) {
        this.rejectedQty = rejectedQty;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(final BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
