package com.mall.api.supplier.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SupplierDTO {

    private Long id;
    private String supplierNo;
    private String supplierName;
    private Long cityId;
    private Long status;
    private Long auditStatus;
    private BigDecimal depositRequiredAmount;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(final String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(final String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }

    public BigDecimal getDepositRequiredAmount() {
        return depositRequiredAmount;
    }

    public void setDepositRequiredAmount(final BigDecimal depositRequiredAmount) {
        this.depositRequiredAmount = depositRequiredAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
