package com.mall.api.trade.vo;

public class FulfillmentTrackQueryVO {

    private Long orderId;
    private Long orderItemId;
    private Long nodeType;
    private Long status;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(final Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getNodeType() {
        return nodeType;
    }

    public void setNodeType(final Long nodeType) {
        this.nodeType = nodeType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
