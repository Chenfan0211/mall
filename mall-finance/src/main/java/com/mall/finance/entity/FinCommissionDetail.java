package com.mall.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;

@TableName("fin_commission_detail")
public class FinCommissionDetail extends BaseEntity {

    private String commissionNo;
    private Long orderId;
    private Long orderItemId;
    private Long subjectType;
    private Long subjectId;
    private Long supplierId;
    private Long stationId;
    private BigDecimal commissionAmount;
    private Long status;

    public String getCommissionNo() {
        return commissionNo;
    }

    public void setCommissionNo(final String commissionNo) {
        this.commissionNo = commissionNo;
    }

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

    public Long getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(final Long subjectType) {
        this.subjectType = subjectType;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(final Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(final BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
