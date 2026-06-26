package com.mall.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.aftersale.convert.AfterSaleConvert;
import com.mall.aftersale.entity.AfsAfterSaleItem;
import com.mall.aftersale.mapper.AfsAfterSaleItemMapper;
import com.mall.aftersale.service.AfterSaleItemService;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.aftersale.vo.AfterSaleItemQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AfterSaleItemServiceImpl implements AfterSaleItemService {

    private final AfsAfterSaleItemMapper afterSaleItemMapper;
    private final AfterSaleConvert afterSaleConvert;

    public AfterSaleItemServiceImpl(
            final AfsAfterSaleItemMapper afterSaleItemMapper,
            final AfterSaleConvert afterSaleConvert) {
        this.afterSaleItemMapper = afterSaleItemMapper;
        this.afterSaleConvert = afterSaleConvert;
    }

    @Override
    public PageResult<AfterSaleItemDTO> pageItems(final AfterSaleItemQueryVO query) {
        final AfterSaleItemQueryVO safeQuery = query == null ? new AfterSaleItemQueryVO() : query;
        final LambdaQueryWrapper<AfsAfterSaleItem> wrapper = new LambdaQueryWrapper<AfsAfterSaleItem>()
                .eq(safeQuery.getAfterSaleId() != null, AfsAfterSaleItem::getAfterSaleId,
                        safeQuery.getAfterSaleId())
                .eq(safeQuery.getOrderItemId() != null, AfsAfterSaleItem::getOrderItemId,
                        safeQuery.getOrderItemId())
                .eq(safeQuery.getSkuId() != null, AfsAfterSaleItem::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getSupplierId() != null, AfsAfterSaleItem::getSupplierId, safeQuery.getSupplierId())
                .orderByDesc(AfsAfterSaleItem::getId);
        final Page<AfsAfterSaleItem> page = afterSaleItemMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(afterSaleConvert::toAfterSaleItemDTO)
                .toList());
    }

    @Override
    public AfterSaleItemDTO getItem(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "售后商品ID不能为空");
        }
        final AfsAfterSaleItem item = afterSaleItemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "售后商品不存在");
        }
        return afterSaleConvert.toAfterSaleItemDTO(item);
    }
}
