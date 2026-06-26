package com.mall.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderStatusLogDTO;
import com.mall.api.trade.vo.OrderQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdOrder;
import com.mall.trade.entity.OrdOrderStatusLog;
import com.mall.trade.mapper.OrdOrderMapper;
import com.mall.trade.mapper.OrdOrderStatusLogMapper;
import com.mall.trade.service.OrderService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrdOrderMapper orderMapper;
    private final OrdOrderStatusLogMapper statusLogMapper;
    private final TradeConvert tradeConvert;

    public OrderServiceImpl(
            final OrdOrderMapper orderMapper,
            final OrdOrderStatusLogMapper statusLogMapper,
            final TradeConvert tradeConvert) {
        this.orderMapper = orderMapper;
        this.statusLogMapper = statusLogMapper;
        this.tradeConvert = tradeConvert;
    }

    @Override
    public PageResult<OrderDTO> pageOrders(final OrderQueryVO query) {
        final OrderQueryVO safeQuery = query == null ? new OrderQueryVO() : query;
        final LambdaQueryWrapper<OrdOrder> wrapper = new LambdaQueryWrapper<OrdOrder>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(OrdOrder::getOrderNo, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrder::getPickupName, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrder::getPickupMobile, safeQuery.getKeyword()))
                .eq(safeQuery.getUserId() != null, OrdOrder::getUserId, safeQuery.getUserId())
                .eq(safeQuery.getCityId() != null, OrdOrder::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStationId() != null, OrdOrder::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getLeaderId() != null, OrdOrder::getLeaderId, safeQuery.getLeaderId())
                .eq(safeQuery.getTradeStatus() != null, OrdOrder::getTradeStatus, safeQuery.getTradeStatus())
                .eq(safeQuery.getPayStatus() != null, OrdOrder::getPayStatus, safeQuery.getPayStatus())
                .eq(safeQuery.getFulfillStatus() != null, OrdOrder::getFulfillStatus, safeQuery.getFulfillStatus())
                .orderByDesc(OrdOrder::getId);
        final Page<OrdOrder> page = orderMapper.selectPage(Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()),
                wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream().map(tradeConvert::toOrderDTO).toList());
    }

    @Override
    public OrderDTO getOrder(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "订单ID不能为空");
        }
        final OrdOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "订单不存在");
        }
        return tradeConvert.toOrderDTO(order);
    }

    @Override
    public List<OrderStatusLogDTO> listStatusLogs(final Long orderId) {
        if (orderId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "订单ID不能为空");
        }
        final LambdaQueryWrapper<OrdOrderStatusLog> wrapper = new LambdaQueryWrapper<OrdOrderStatusLog>()
                .eq(OrdOrderStatusLog::getOrderId, orderId)
                .orderByAsc(OrdOrderStatusLog::getCreateTime)
                .orderByAsc(OrdOrderStatusLog::getId);
        return statusLogMapper.selectList(wrapper).stream().map(tradeConvert::toOrderStatusLogDTO).toList();
    }
}
