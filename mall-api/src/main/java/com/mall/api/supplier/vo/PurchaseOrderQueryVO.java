package com.mall.api.supplier.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class PurchaseOrderQueryVO extends PageQuery {

    private String keyword;
    private Long sourceType;
    private Long supplierId;
    private Long warehouseId;
    private Long cityId;
    private Long purchaseStatus;
    private Long auditStatus;
    private LocalDate deliveryDate;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getSourceType() {
        return sourceType;
    }

    public void setSourceType(final Long sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(final Long purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
