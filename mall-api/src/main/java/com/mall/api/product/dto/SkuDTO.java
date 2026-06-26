package com.mall.api.product.dto;

import java.time.LocalDateTime;

public class SkuDTO {

    private Long id;
    private String skuNo;
    private Long productId;
    private String skuName;
    private String barcode;
    private String saleSpecText;
    private String baseUnit;
    private Long supplierId;
    private Long weightGram;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSkuNo() {
        return skuNo;
    }

    public void setSkuNo(final String skuNo) {
        this.skuNo = skuNo;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public String getSaleSpecText() {
        return saleSpecText;
    }

    public void setSaleSpecText(final String saleSpecText) {
        this.saleSpecText = saleSpecText;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(final String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getWeightGram() {
        return weightGram;
    }

    public void setWeightGram(final Long weightGram) {
        this.weightGram = weightGram;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
