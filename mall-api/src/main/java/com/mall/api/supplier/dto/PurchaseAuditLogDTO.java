package com.mall.api.supplier.dto;

import java.time.LocalDateTime;

public class PurchaseAuditLogDTO {

    private Long id;
    private Long purchaseId;
    private Long auditAction;
    private Long beforeStatus;
    private Long afterStatus;
    private Long auditAccountId;
    private String auditRemark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(final Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getAuditAction() {
        return auditAction;
    }

    public void setAuditAction(final Long auditAction) {
        this.auditAction = auditAction;
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

    public Long getAuditAccountId() {
        return auditAccountId;
    }

    public void setAuditAccountId(final Long auditAccountId) {
        this.auditAccountId = auditAccountId;
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
