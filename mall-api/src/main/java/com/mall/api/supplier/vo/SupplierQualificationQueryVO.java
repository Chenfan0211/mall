package com.mall.api.supplier.vo;

import com.mall.common.page.PageQuery;

public class SupplierQualificationQueryVO extends PageQuery {

    private String keyword;
    private Long supplierId;
    private Long qualificationType;
    private Long auditStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(final Long qualificationType) {
        this.qualificationType = qualificationType;
    }

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }
}
