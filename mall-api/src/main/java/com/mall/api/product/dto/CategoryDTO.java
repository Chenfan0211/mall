package com.mall.api.product.dto;

import java.time.LocalDateTime;

public class CategoryDTO {

    private Long id;
    private Long parentId;
    private String categoryCode;
    private String categoryName;
    private String imageUrl;
    private Long categoryType;
    private Long sortNo;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(final String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(final Long categoryType) {
        this.categoryType = categoryType;
    }

    public Long getSortNo() {
        return sortNo;
    }

    public void setSortNo(final Long sortNo) {
        this.sortNo = sortNo;
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
