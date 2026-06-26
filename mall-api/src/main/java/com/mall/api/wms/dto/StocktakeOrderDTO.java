package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class StocktakeOrderDTO {

    private Long id;
    private String stocktakeNo;
    private Long warehouseId;
    private Long skuId;
    private Long bookQty;
    private Long actualQty;
    private Long diffQty;
    private Long status;
    private Long createAccountId;
    private Long auditAccountId;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getStocktakeNo() {
        return stocktakeNo;
    }

    public void setStocktakeNo(final String stocktakeNo) {
        this.stocktakeNo = stocktakeNo;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getBookQty() {
        return bookQty;
    }

    public void setBookQty(final Long bookQty) {
        this.bookQty = bookQty;
    }

    public Long getActualQty() {
        return actualQty;
    }

    public void setActualQty(final Long actualQty) {
        this.actualQty = actualQty;
    }

    public Long getDiffQty() {
        return diffQty;
    }

    public void setDiffQty(final Long diffQty) {
        this.diffQty = diffQty;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getCreateAccountId() {
        return createAccountId;
    }

    public void setCreateAccountId(final Long createAccountId) {
        this.createAccountId = createAccountId;
    }

    public Long getAuditAccountId() {
        return auditAccountId;
    }

    public void setAuditAccountId(final Long auditAccountId) {
        this.auditAccountId = auditAccountId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
