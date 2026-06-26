package com.mall.api.supplier.dto;

import java.time.LocalDateTime;

public class PurchaseSourceLogDTO {

    private Long id;
    private Long purchaseId;
    private Long sourceType;
    private String sourceBizNo;
    private String sourceReason;
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

    public Long getSourceType() {
        return sourceType;
    }

    public void setSourceType(final Long sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceBizNo() {
        return sourceBizNo;
    }

    public void setSourceBizNo(final String sourceBizNo) {
        this.sourceBizNo = sourceBizNo;
    }

    public String getSourceReason() {
        return sourceReason;
    }

    public void setSourceReason(final String sourceReason) {
        this.sourceReason = sourceReason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
