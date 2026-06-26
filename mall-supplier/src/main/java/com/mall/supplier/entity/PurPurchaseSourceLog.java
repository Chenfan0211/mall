package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("pur_purchase_source_log")
public class PurPurchaseSourceLog extends BaseEntity {

    private Long purchaseId;
    private Long sourceType;
    private String sourceBizNo;
    private String sourceReason;

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
}
