package com.mall.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("pay_trade")
public class PayTrade extends BaseEntity {

    private String payNo;
    private Long orderId;
    private String orderNo;
    private Long channelType;
    private BigDecimal payAmount;
    private Long tradeStatus;
    private String channelTradeNo;
    private LocalDateTime payTime;
    private String idempotentKey;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getChannelType() {
        return channelType;
    }

    public void setChannelType(final Long channelType) {
        this.channelType = channelType;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(final BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Long getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(final Long tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getChannelTradeNo() {
        return channelTradeNo;
    }

    public void setChannelTradeNo(final String channelTradeNo) {
        this.channelTradeNo = channelTradeNo;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(final LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(final String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }
}
