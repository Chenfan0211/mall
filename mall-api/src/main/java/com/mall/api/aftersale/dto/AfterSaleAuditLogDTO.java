package com.mall.api.aftersale.dto;

import java.time.LocalDateTime;

public class AfterSaleAuditLogDTO {

    private Long id;
    private Long afterSaleId;
    private Long auditAction;
    private Long beforeStatus;
    private Long afterStatus;
    private Long auditAccountId;
    private String auditReason;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(final Long afterSaleId) {
        this.afterSaleId = afterSaleId;
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

    public String getAuditReason() {
        return auditReason;
    }

    public void setAuditReason(final String auditReason) {
        this.auditReason = auditReason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
