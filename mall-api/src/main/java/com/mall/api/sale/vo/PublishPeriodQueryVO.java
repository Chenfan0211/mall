package com.mall.api.sale.vo;

import com.mall.common.page.PageQuery;

public class PublishPeriodQueryVO extends PageQuery {

    private String keyword;
    private Long regionId;
    private Long cityId;
    private Long warehouseId;
    private Long stockMode;
    private Long status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(final Long regionId) {
        this.regionId = regionId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getStockMode() {
        return stockMode;
    }

    public void setStockMode(final Long stockMode) {
        this.stockMode = stockMode;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
