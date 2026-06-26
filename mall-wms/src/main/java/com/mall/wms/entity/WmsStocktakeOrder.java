package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_stocktake_order")
public class WmsStocktakeOrder extends BaseEntity {

    private String stocktakeNo;
    private Long warehouseId;
    private Long skuId;
    private Long bookQty;
    private Long actualQty;
    private Long diffQty;
    private Long status;
    private Long createAccountId;
    private Long auditAccountId;

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
}
