package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("prd_stock_spec")
public class PrdStockSpec extends BaseEntity {

    private String specCode;
    private String specName;
    private String unitName;
    private Long conversionRate;
    private Long status;

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
}
