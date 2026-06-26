package com.mall.api.product.vo;

import com.mall.common.page.PageQuery;

public class ProductQueryVO extends PageQuery {

    private String keyword;
    private Long supplierId;
    private Long frontCategoryId;
    private Long statCategoryId;
    private Long auditStatus;
    private Long saleStatus;
    private Long cityId;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getFrontCategoryId() {
        return frontCategoryId;
    }

    public void setFrontCategoryId(final Long frontCategoryId) {
        this.frontCategoryId = frontCategoryId;
    }

    public Long getStatCategoryId() {
        return statCategoryId;
    }

    public void setStatCategoryId(final Long statCategoryId) {
        this.statCategoryId = statCategoryId;
    }

    public Long getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(final Long auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(final Long saleStatus) {
        this.saleStatus = saleStatus;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }
}
