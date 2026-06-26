package com.mall.station.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;

@TableName("fin_account")
public class FinAccount extends BaseEntity {

    private String accountNo;
    private Long subjectType;
    private Long subjectId;
    private BigDecimal availableAmount;
    private BigDecimal frozenAmount;
    private BigDecimal depositAmount;
    private Long status;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(final String accountNo) {
        this.accountNo = accountNo;
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

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(final BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(final BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(final BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
