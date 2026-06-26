package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDate;

@TableName("sup_qualification")
public class SupQualification extends BaseEntity {

    private String qualificationNo;
    private Long supplierId;
    private Long qualificationType;
    private String certificateNo;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    private Long fileAssetId;
    private Long auditStatus;

    public String getQualificationNo() {
        return qualificationNo;
    }

    public void setQualificationNo(final String qualificationNo) {
        this.qualificationNo = qualificationNo;
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

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(final String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public LocalDate getValidStartDate() {
        return validStartDate;
    }

    public void setValidStartDate(final LocalDate validStartDate) {
        this.validStartDate = validStartDate;
    }

    public LocalDate getValidEndDate() {
        return validEndDate;
    }

    public void setValidEndDate(final LocalDate validEndDate) {
        this.validEndDate = validEndDate;
    }

    public Long getFileAssetId() {
        return fileAssetId;
    }

    public void setFileAssetId(final Long fileAssetId) {
        this.fileAssetId = fileAssetId;
    }

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }
}
