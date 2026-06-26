package com.mall.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("fin_supplier_settlement")
public class FinSupplierSettlement extends BaseEntity {

    private String settlementNo;
    private Long supplierId;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private BigDecimal purchaseAmount;
    private BigDecimal diffAmount;
    private BigDecimal payableAmount;
    private Long status;

    public String getSettlementNo() {
        return settlementNo;
    }

    public void setSettlementNo(final String settlementNo) {
        this.settlementNo = settlementNo;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(final LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(final LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(final BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public BigDecimal getDiffAmount() {
        return diffAmount;
    }

    public void setDiffAmount(final BigDecimal diffAmount) {
        this.diffAmount = diffAmount;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(final BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
