package com.mall.api.user.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class UserProductQueryVO extends PageQuery {

    private Long userId;
    private Long cityId;
    private Long stationId;
    private Long categoryId;
    private Long productId;
    private Long periodId;
    private String keyword;
    private LocalDate deliveryDate;
    private String sortField;
    private String sortOrder;
    private Boolean hasImage;
    private Boolean mergeSku;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(final Long periodId) {
        this.periodId = periodId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(final String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(final String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(final Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Boolean getMergeSku() {
        return mergeSku;
    }

    public void setMergeSku(final Boolean mergeSku) {
        this.mergeSku = mergeSku;
    }
}
