package com.mall.api.product.vo;

public class CategoryQueryVO {

    private Long categoryType;
    private Long parentId;
    private Long status;

    public Long getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(final Long categoryType) {
        this.categoryType = categoryType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
