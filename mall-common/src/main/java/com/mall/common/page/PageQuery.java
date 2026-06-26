package com.mall.common.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class PageQuery {

    @Min(value = 1, message = "页码必须大于0")
    private Long pageNum = 1L;

    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 200, message = "每页数量不能超过200")
    private Long pageSize = 20L;

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(final Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Long pageSize) {
        this.pageSize = pageSize;
    }
}
