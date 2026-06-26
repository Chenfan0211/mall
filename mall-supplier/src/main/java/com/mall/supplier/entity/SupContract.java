package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("sup_contract")
public class SupContract extends BaseEntity {

    private String contractNo;
    private Long supplierId;
    private String contractName;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    private BigDecimal depositAmount;
    private Long fileAssetId;
    private Long status;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(final String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(final String contractName) {
        this.contractName = contractName;
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

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(final BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public Long getFileAssetId() {
        return fileAssetId;
    }

    public void setFileAssetId(final Long fileAssetId) {
        this.fileAssetId = fileAssetId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
