package com.mall.aftersale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("afs_audit_log")
public class AfsAuditLog extends BaseEntity {

    private Long afterSaleId;
    private Long auditAction;
    private Long beforeStatus;
    private Long afterStatus;
    private Long auditAccountId;
    private String auditReason;

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
}
