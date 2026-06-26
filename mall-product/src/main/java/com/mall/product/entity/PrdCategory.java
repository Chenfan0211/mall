package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("prd_category")
public class PrdCategory extends BaseEntity {

    private Long parentId;
    private String categoryCode;
    private String categoryName;
    private Long categoryType;
    private Long sortNo;
    private Long status;

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
}
