package com.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.product.vo.ProductQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.product.convert.ProductConvert;
import com.mall.product.entity.PrdProduct;
import com.mall.product.mapper.PrdProductMapper;
import com.mall.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final PrdProductMapper productMapper;
    private final ProductConvert productConvert;

    public ProductServiceImpl(final PrdProductMapper productMapper, final ProductConvert productConvert) {
        this.productMapper = productMapper;
        this.productConvert = productConvert;
    }

    @Override
    public PageResult<ProductDTO> pageProducts(final ProductQueryVO query) {
        final ProductQueryVO safeQuery = query == null ? new ProductQueryVO() : query;
        final LambdaQueryWrapper<PrdProduct> wrapper = new LambdaQueryWrapper<PrdProduct>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(PrdProduct::getProductName, safeQuery.getKeyword())
                        .or()
                        .like(PrdProduct::getProductNo, safeQuery.getKeyword()))
                .eq(safeQuery.getSupplierId() != null, PrdProduct::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getFrontCategoryId() != null, PrdProduct::getFrontCategoryId,
                        safeQuery.getFrontCategoryId())
                .eq(safeQuery.getStatCategoryId() != null, PrdProduct::getStatCategoryId,
                        safeQuery.getStatCategoryId())
                .eq(safeQuery.getAuditStatus() != null, PrdProduct::getAuditStatus, safeQuery.getAuditStatus())
                .eq(safeQuery.getSaleStatus() != null, PrdProduct::getSaleStatus, safeQuery.getSaleStatus())
                .eq(safeQuery.getCityId() != null, PrdProduct::getCityId, safeQuery.getCityId())
                .orderByDesc(PrdProduct::getId);
        final Page<PrdProduct> page = productMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream().map(productConvert::toProductDTO).toList());
    }

    @Override
    public ProductDTO getProduct(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        final PrdProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "商品不存在");
        }
        return productConvert.toProductDTO(product);
    }
}
