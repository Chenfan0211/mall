package com.mall.api.payment.vo;

import com.mall.common.page.PageQuery;

public class RefundTradeQueryVO extends PageQuery {

    private String keyword;
    private String payNo;
    private Long orderId;
    private Long afterSaleId;
    private Long refundStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
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

    public Long getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(final Long refundStatus) {
        this.refundStatus = refundStatus;
    }
}
