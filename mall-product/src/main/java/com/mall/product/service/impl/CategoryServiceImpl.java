package com.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.vo.CategoryQueryVO;
import com.mall.product.convert.ProductConvert;
import com.mall.product.entity.PrdCategory;
import com.mall.product.mapper.PrdCategoryMapper;
import com.mall.product.service.CategoryService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final PrdCategoryMapper categoryMapper;
    private final ProductConvert productConvert;

    public CategoryServiceImpl(final PrdCategoryMapper categoryMapper, final ProductConvert productConvert) {
        this.categoryMapper = categoryMapper;
        this.productConvert = productConvert;
    }

    @Override
    public List<CategoryDTO> listCategories(final CategoryQueryVO query) {
        final CategoryQueryVO safeQuery = query == null ? new CategoryQueryVO() : query;
        final LambdaQueryWrapper<PrdCategory> wrapper = new LambdaQueryWrapper<PrdCategory>()
                .eq(safeQuery.getCategoryType() != null, PrdCategory::getCategoryType, safeQuery.getCategoryType())
                .eq(safeQuery.getParentId() != null, PrdCategory::getParentId, safeQuery.getParentId())
                .eq(safeQuery.getStatus() != null, PrdCategory::getStatus, safeQuery.getStatus())
                .orderByAsc(PrdCategory::getCategoryType)
                .orderByAsc(PrdCategory::getParentId)
                .orderByAsc(PrdCategory::getSortNo)
                .orderByDesc(PrdCategory::getId);
        return categoryMapper.selectList(wrapper).stream().map(productConvert::toCategoryDTO).toList();
    }
}
