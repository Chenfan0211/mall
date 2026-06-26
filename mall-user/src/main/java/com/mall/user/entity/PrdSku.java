package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("prd_sku")
public class PrdSku extends BaseEntity {

    private String skuNo;
    private Long productId;
    private String skuName;
    private String barcode;
    private String saleSpecText;
    private String baseUnit;
    private Long supplierId;
    private Long weightGram;
    private Long status;

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
}
