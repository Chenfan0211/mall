package com.mall.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.trade.vo.OrderItemQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdOrderItem;
import com.mall.trade.mapper.OrdOrderItemMapper;
import com.mall.trade.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderItemServiceImpl implements OrderItemService {

    private final OrdOrderItemMapper orderItemMapper;
    private final TradeConvert tradeConvert;

    public OrderItemServiceImpl(final OrdOrderItemMapper orderItemMapper, final TradeConvert tradeConvert) {
        this.orderItemMapper = orderItemMapper;
        this.tradeConvert = tradeConvert;
    }

    @Override
    public PageResult<OrderItemDTO> pageOrderItems(final OrderItemQueryVO query) {
        final OrderItemQueryVO safeQuery = query == null ? new OrderItemQueryVO() : query;
        final LambdaQueryWrapper<OrdOrderItem> wrapper = new LambdaQueryWrapper<OrdOrderItem>()
                .eq(safeQuery.getOrderId() != null, OrdOrderItem::getOrderId, safeQuery.getOrderId())
                .eq(safeQuery.getPeriodId() != null, OrdOrderItem::getPeriodId, safeQuery.getPeriodId())
                .eq(safeQuery.getProductId() != null, OrdOrderItem::getProductId, safeQuery.getProductId())
                .eq(safeQuery.getSkuId() != null, OrdOrderItem::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getSupplierId() != null, OrdOrderItem::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getWarehouseId() != null, OrdOrderItem::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getCityId() != null, OrdOrderItem::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStationId() != null, OrdOrderItem::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getFulfillStatus() != null, OrdOrderItem::getFulfillStatus,
                        safeQuery.getFulfillStatus())
                .eq(safeQuery.getAfterSaleStatus() != null, OrdOrderItem::getAfterSaleStatus,
                        safeQuery.getAfterSaleStatus())
                .orderByDesc(OrdOrderItem::getId);
        final Page<OrdOrderItem> page = orderItemMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream().map(tradeConvert::toOrderItemDTO).toList());
    }

    @Override
    public OrderItemDTO getOrderItem(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "订单商品ID不能为空");
        }
        final OrdOrderItem orderItem = orderItemMapper.selectById(id);
        if (orderItem == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "订单商品不存在");
        }
        return tradeConvert.toOrderItemDTO(orderItem);
    }
}
