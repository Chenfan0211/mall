package com.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageQuery;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import java.util.function.Function;

abstract class WmsReadSupport {

    protected <E, D> PageResult<D> toPage(
            final PageQuery query,
            final BaseMapper<E> mapper,
            final com.baomidou.mybatisplus.core.conditions.Wrapper<E> wrapper,
            final Function<E, D> converter) {
        final Page<E> page = mapper.selectPage(Page.of(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream().map(converter).toList());
    }

    protected <E, D> D getById(
            final Long id,
            final BaseMapper<E> mapper,
            final Function<E, D> converter,
            final String resourceName) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, resourceName + " id is required");
        }
        final E entity = mapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, resourceName + " not found");
        }
        return converter.apply(entity);
    }
}
