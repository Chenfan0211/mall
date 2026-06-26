package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("sale_publish_period")
public class SalePublishPeriod extends BaseEntity {

    private String periodNo;
    private String periodName;
    private Long regionId;
    private Long cityId;
    private Long warehouseId;
    private LocalDateTime saleStartTime;
    private LocalDateTime saleEndTime;
    private LocalDateTime actualCutoffTime;
    private LocalDate deliveryDate;
    private Long stockMode;
    private Long status;
    private Long publishAccountId;
    private LocalDateTime publishTime;

    public String getPeriodNo() {
        return periodNo;
    }

    public void setPeriodNo(final String periodNo) {
        this.periodNo = periodNo;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(final String periodName) {
        this.periodName = periodName;
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

    public LocalDateTime getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(final LocalDateTime saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public LocalDateTime getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(final LocalDateTime saleEndTime) {
        this.saleEndTime = saleEndTime;
    }

    public LocalDateTime getActualCutoffTime() {
        return actualCutoffTime;
    }

    public void setActualCutoffTime(final LocalDateTime actualCutoffTime) {
        this.actualCutoffTime = actualCutoffTime;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
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

    public Long getPublishAccountId() {
        return publishAccountId;
    }

    public void setPublishAccountId(final Long publishAccountId) {
        this.publishAccountId = publishAccountId;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(final LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
}
