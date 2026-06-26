package com.mall.api.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawApplyDTO {

    private Long id;
    private String withdrawNo;
    private Long accountId;
    private Long subjectType;
    private Long subjectId;
    private BigDecimal withdrawAmount;
    private String receiveAccountName;
    private String receiveAccountNo;
    private Long auditStatus;
    private Long payStatus;
    private Long auditAccountId;
    private LocalDateTime auditTime;
    private String auditRemark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getWithdrawNo() {
        return withdrawNo;
    }

    public void setWithdrawNo(final String withdrawNo) {
        this.withdrawNo = withdrawNo;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
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

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(final BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getReceiveAccountName() {
        return receiveAccountName;
    }

    public void setReceiveAccountName(final String receiveAccountName) {
        this.receiveAccountName = receiveAccountName;
    }

    public String getReceiveAccountNo() {
        return receiveAccountNo;
    }

    public void setReceiveAccountNo(final String receiveAccountNo) {
        this.receiveAccountNo = receiveAccountNo;
    }

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(final Long payStatus) {
        this.payStatus = payStatus;
    }

    public Long getAuditAccountId() {
        return auditAccountId;
    }

    public void setAuditAccountId(final Long auditAccountId) {
        this.auditAccountId = auditAccountId;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(final LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(final String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
