package com.mall.api.wms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InboundItemDTO {

    private Long id;
    private Long inboundId;
    private Long skuId;
    private Long supplierId;
    private Long expectedQty;
    private Long receivedQty;
    private Long rejectedQty;
    private Long damagedQty;
    private String batchNo;
    private LocalDate productionDate;
    private Long shelfLifeDays;
    private String diffReason;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(final Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getExpectedQty() {
        return expectedQty;
    }

    public void setExpectedQty(final Long expectedQty) {
        this.expectedQty = expectedQty;
    }

    public Long getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(final Long receivedQty) {
        this.receivedQty = receivedQty;
    }

    public Long getRejectedQty() {
        return rejectedQty;
    }

    public void setRejectedQty(final Long rejectedQty) {
        this.rejectedQty = rejectedQty;
    }

    public Long getDamagedQty() {
        return damagedQty;
    }

    public void setDamagedQty(final Long damagedQty) {
        this.damagedQty = damagedQty;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(final String batchNo) {
        this.batchNo = batchNo;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(final LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public Long getShelfLifeDays() {
        return shelfLifeDays;
    }

    public void setShelfLifeDays(final Long shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

    public String getDiffReason() {
        return diffReason;
    }

    public void setDiffReason(final String diffReason) {
        this.diffReason = diffReason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
