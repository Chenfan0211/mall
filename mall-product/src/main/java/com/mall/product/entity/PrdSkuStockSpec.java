package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("prd_sku_stock_spec")
public class PrdSkuStockSpec extends BaseEntity {

    private Long skuId;
    private Long stockSpecId;
    private Long conversionRate;
    private Long defaultFlag;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getStockSpecId() {
        return stockSpecId;
    }

    public void setStockSpecId(final Long stockSpecId) {
        this.stockSpecId = stockSpecId;
    }

    public Long getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(final Long conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Long getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(final Long defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
}
