package com.mall.api.wms.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class WmsInboundQueryVO extends PageQuery {

    private String keyword;
    private Long sourceType;
    private Long supplierId;
    private Long warehouseId;
    private Long inboundId;
    private Long inboundItemId;
    private Long skuId;
    private Long qualityStatus;
    private Long qualityResult;
    private Long status;
    private LocalDate createDate;

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

    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(final Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getInboundItemId() {
        return inboundItemId;
    }

    public void setInboundItemId(final Long inboundItemId) {
        this.inboundItemId = inboundItemId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getQualityStatus() {
        return qualityStatus;
    }

    public void setQualityStatus(final Long qualityStatus) {
        this.qualityStatus = qualityStatus;
    }

    public Long getQualityResult() {
        return qualityResult;
    }

    public void setQualityResult(final Long qualityResult) {
        this.qualityResult = qualityResult;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(final LocalDate createDate) {
        this.createDate = createDate;
    }
}
