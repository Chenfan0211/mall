package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.domain.BaseEntity;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageQuery;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import java.time.LocalDateTime;
import java.util.function.Function;

abstract class UserReadSupport {

    protected <E, D> PageResult<D> toPage(
            final PageQuery query,
            final BaseMapper<E> mapper,
            final Wrapper<E> wrapper,
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
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, resourceName + "ID不能为空");
        }
        final E entity = mapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, resourceName + "不存在");
        }
        return converter.apply(entity);
    }

    protected void prepareCreate(final BaseEntity entity, final Long id, final LocalDateTime now) {
        entity.setId(id);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setDeleted(0L);
    }

    protected void prepareUpdate(final BaseEntity entity, final LocalDateTime now) {
        entity.setUpdateTime(now);
    }
}
