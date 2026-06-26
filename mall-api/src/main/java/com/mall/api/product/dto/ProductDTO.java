package com.mall.api.product.dto;

import java.time.LocalDateTime;

public class ProductDTO {

    private Long id;
    private String productNo;
    private String productName;
    private Long frontCategoryId;
    private Long statCategoryId;
    private Long supplierId;
    private String mainImageUrl;
    private String imageJson;
    private Long auditStatus;
    private Long saleStatus;
    private Long cityId;
    private Long createAccountId;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(final String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(final String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getImageJson() {
        return imageJson;
    }

    public void setImageJson(final String imageJson) {
        this.imageJson = imageJson;
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

    public Long getCreateAccountId() {
        return createAccountId;
    }

    public void setCreateAccountId(final Long createAccountId) {
        this.createAccountId = createAccountId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
