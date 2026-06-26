package com.mall.api.supplier.dto;

import java.time.LocalDateTime;

public class DeliveryItemDTO {

    private Long id;
    private Long deliveryId;
    private Long purchaseItemId;
    private Long skuId;
    private Long deliveryQty;
    private Long receivedQty;
    private Long diffQty;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(final Long purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getDeliveryQty() {
        return deliveryQty;
    }

    public void setDeliveryQty(final Long deliveryQty) {
        this.deliveryQty = deliveryQty;
    }

    public Long getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(final Long receivedQty) {
        this.receivedQty = receivedQty;
    }

    public Long getDiffQty() {
        return diffQty;
    }

    public void setDiffQty(final Long diffQty) {
        this.diffQty = diffQty;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
