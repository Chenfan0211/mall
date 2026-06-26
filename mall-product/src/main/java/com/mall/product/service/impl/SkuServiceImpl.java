package com.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.product.dto.SkuDTO;
import com.mall.api.product.dto.StockSpecDTO;
import com.mall.api.product.vo.SkuQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.product.convert.ProductConvert;
import com.mall.product.entity.PrdSku;
import com.mall.product.mapper.PrdSkuMapper;
import com.mall.product.mapper.PrdStockSpecMapper;
import com.mall.product.service.SkuService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SkuServiceImpl implements SkuService {

    private final PrdSkuMapper skuMapper;
    private final PrdStockSpecMapper stockSpecMapper;
    private final ProductConvert productConvert;

    public SkuServiceImpl(
            final PrdSkuMapper skuMapper,
            final PrdStockSpecMapper stockSpecMapper,
            final ProductConvert productConvert) {
        this.skuMapper = skuMapper;
        this.stockSpecMapper = stockSpecMapper;
        this.productConvert = productConvert;
    }

    @Override
    public List<SkuDTO> listSkus(final SkuQueryVO query) {
        final SkuQueryVO safeQuery = query == null ? new SkuQueryVO() : query;
        final LambdaQueryWrapper<PrdSku> wrapper = new LambdaQueryWrapper<PrdSku>()
                .eq(safeQuery.getProductId() != null, PrdSku::getProductId, safeQuery.getProductId())
                .eq(safeQuery.getSupplierId() != null, PrdSku::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getStatus() != null, PrdSku::getStatus, safeQuery.getStatus())
                .orderByDesc(PrdSku::getId);
        return skuMapper.selectList(wrapper).stream().map(productConvert::toSkuDTO).toList();
    }

    @Override
    public SkuDTO getSku(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "SKU ID不能为空");
        }
        final PrdSku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "SKU不存在");
        }
        return productConvert.toSkuDTO(sku);
    }

    @Override
    public List<StockSpecDTO> listStockSpecs(final Long skuId) {
        if (skuId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "SKU ID不能为空");
        }
        return stockSpecMapper.selectBySkuId(skuId);
    }
}
