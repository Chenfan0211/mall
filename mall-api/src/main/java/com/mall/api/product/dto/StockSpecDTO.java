package com.mall.api.product.dto;

public class StockSpecDTO {

    private Long id;
    private String specCode;
    private String specName;
    private String unitName;
    private Long conversionRate;
    private Long status;
    private Long defaultFlag;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSpecCode() {
        return specCode;
    }

    public void setSpecCode(final String specCode) {
        this.specCode = specCode;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(final String specName) {
        this.specName = specName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(final String unitName) {
        this.unitName = unitName;
    }

    public Long getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(final Long conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Long getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(final Long defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
}
