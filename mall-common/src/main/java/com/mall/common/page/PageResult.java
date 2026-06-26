package com.mall.common.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PageResult<T> implements Serializable {

    private Long total;
    private List<T> list;

    public static <T> PageResult<T> of(final Long total, final List<T> list) {
        final PageResult<T> result = new PageResult<>();
        result.setTotal(total == null ? 0L : total);
        result.setList(list == null ? Collections.emptyList() : list);
        return result;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(final Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(final List<T> list) {
        this.list = list;
    }
}
