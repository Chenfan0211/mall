package com.mall.api.supplier.vo;

import com.mall.common.page.PageQuery;

public class DeliveryItemQueryVO extends PageQuery {

    private Long deliveryId;
    private Long purchaseItemId;
    private Long skuId;

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
}
