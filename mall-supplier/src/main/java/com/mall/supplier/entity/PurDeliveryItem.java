package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("pur_delivery_item")
public class PurDeliveryItem extends BaseEntity {

    private Long deliveryId;
    private Long purchaseItemId;
    private Long skuId;
    private Long deliveryQty;
    private Long receivedQty;
    private Long diffQty;

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
}
