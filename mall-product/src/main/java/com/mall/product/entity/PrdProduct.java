package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("prd_product")
public class PrdProduct extends BaseEntity {

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
}
