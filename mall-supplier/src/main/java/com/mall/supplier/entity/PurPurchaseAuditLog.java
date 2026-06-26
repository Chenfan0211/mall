package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("pur_purchase_audit_log")
public class PurPurchaseAuditLog extends BaseEntity {

    private Long purchaseId;
    private Long auditAction;
    private Long beforeStatus;
    private Long afterStatus;
    private Long auditAccountId;
    private String auditRemark;

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
}
