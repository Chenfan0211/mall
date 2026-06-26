package com.mall.api.supplier.dto;

public class SupplierWorkbenchDTO {

    private Long supplierId;
    private Long waitAuditPurchaseCount;
    private Long waitDeliveryPurchaseCount;
    private Long deliveringOrderCount;
    private Long arrivedOrderCount;
    private Long todayDeliveryOrderCount;
    private Long purchaseQty;
    private Long receivedQty;
    private Long diffQty;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getWaitAuditPurchaseCount() {
        return waitAuditPurchaseCount;
    }

    public void setWaitAuditPurchaseCount(final Long waitAuditPurchaseCount) {
        this.waitAuditPurchaseCount = waitAuditPurchaseCount;
    }

    public Long getWaitDeliveryPurchaseCount() {
        return waitDeliveryPurchaseCount;
    }

    public void setWaitDeliveryPurchaseCount(final Long waitDeliveryPurchaseCount) {
        this.waitDeliveryPurchaseCount = waitDeliveryPurchaseCount;
    }

    public Long getDeliveringOrderCount() {
        return deliveringOrderCount;
    }

    public void setDeliveringOrderCount(final Long deliveringOrderCount) {
        this.deliveringOrderCount = deliveringOrderCount;
    }

    public Long getArrivedOrderCount() {
        return arrivedOrderCount;
    }

    public void setArrivedOrderCount(final Long arrivedOrderCount) {
        this.arrivedOrderCount = arrivedOrderCount;
    }

    public Long getTodayDeliveryOrderCount() {
        return todayDeliveryOrderCount;
    }

    public void setTodayDeliveryOrderCount(final Long todayDeliveryOrderCount) {
        this.todayDeliveryOrderCount = todayDeliveryOrderCount;
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

    public Long getDiffQty() {
        return diffQty;
    }

    public void setDiffQty(final Long diffQty) {
        this.diffQty = diffQty;
    }
}
