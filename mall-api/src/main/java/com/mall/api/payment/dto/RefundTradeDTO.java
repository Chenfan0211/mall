package com.mall.api.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundTradeDTO {

    private Long id;
    private String refundNo;
    private String payNo;
    private Long orderId;
    private Long afterSaleId;
    private BigDecimal refundAmount;
    private Long refundStatus;
    private String channelRefundNo;
    private LocalDateTime refundTime;
    private String failReason;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(final String refundNo) {
        this.refundNo = refundNo;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(final String payNo) {
        this.payNo = payNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(final Long afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(final BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Long getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(final Long refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getChannelRefundNo() {
        return channelRefundNo;
    }

    public void setChannelRefundNo(final String channelRefundNo) {
        this.channelRefundNo = channelRefundNo;
    }

    public LocalDateTime getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(final LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(final String failReason) {
        this.failReason = failReason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
