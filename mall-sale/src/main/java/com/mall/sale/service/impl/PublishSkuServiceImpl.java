package com.mall.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.sale.dto.PublishSkuDTO;
import com.mall.api.sale.vo.PublishSkuQueryVO;
import com.mall.sale.convert.SaleConvert;
import com.mall.sale.entity.SalePublishSku;
import com.mall.sale.mapper.SalePublishSkuMapper;
import com.mall.sale.service.PublishSkuService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PublishSkuServiceImpl implements PublishSkuService {

    private final SalePublishSkuMapper publishSkuMapper;
    private final SaleConvert saleConvert;

    public PublishSkuServiceImpl(final SalePublishSkuMapper publishSkuMapper, final SaleConvert saleConvert) {
        this.publishSkuMapper = publishSkuMapper;
        this.saleConvert = saleConvert;
    }

    @Override
    public List<PublishSkuDTO> listPublishSkus(final PublishSkuQueryVO query) {
        final PublishSkuQueryVO safeQuery = query == null ? new PublishSkuQueryVO() : query;
        final LambdaQueryWrapper<SalePublishSku> wrapper = new LambdaQueryWrapper<SalePublishSku>()
                .eq(safeQuery.getPeriodId() != null, SalePublishSku::getPeriodId, safeQuery.getPeriodId())
                .eq(safeQuery.getProductId() != null, SalePublishSku::getProductId, safeQuery.getProductId())
                .eq(safeQuery.getSkuId() != null, SalePublishSku::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getSupplierId() != null, SalePublishSku::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getStatus() != null, SalePublishSku::getStatus, safeQuery.getStatus())
                .orderByDesc(SalePublishSku::getId);
        return publishSkuMapper.selectList(wrapper).stream().map(saleConvert::toPublishSkuDTO).toList();
    }
}
