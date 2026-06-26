package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("ord_order_status_log")
public class OrdOrderStatusLog extends BaseEntity {

    private Long orderId;
    private Long orderItemId;
    private Long statusLine;
    private Long beforeStatus;
    private Long afterStatus;
    private String changeReason;
    private Long operatorType;
    private Long operatorId;

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

    public Long getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(final Long statusLine) {
        this.statusLine = statusLine;
    }

    public Long getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(final Long beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public Long getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(final Long afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(final String changeReason) {
        this.changeReason = changeReason;
    }

    public Long getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(final Long operatorType) {
        this.operatorType = operatorType;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(final Long operatorId) {
        this.operatorId = operatorId;
    }
}
