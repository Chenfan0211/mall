package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.user.vo.UserOrderCancelVO;
import com.mall.common.exception.BusinessException;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.OrdOrder;
import com.mall.user.mapper.OrdCartMapper;
import com.mall.user.mapper.OrdFulfillmentTrackMapper;
import com.mall.user.mapper.OrdOrderItemMapper;
import com.mall.user.mapper.OrdOrderMapper;
import com.mall.user.mapper.OrdOrderStatusLogMapper;
import com.mall.user.mapper.PayTradeMapper;
import com.mall.user.mapper.PrdProductMapper;
import com.mall.user.mapper.PrdSkuMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserOrderServiceImplTest {

    @Mock
    private OrdOrderMapper orderMapper;

    @Mock
    private OrdOrderItemMapper orderItemMapper;

    @Mock
    private OrdFulfillmentTrackMapper fulfillmentTrackMapper;

    @Mock
    private OrdCartMapper cartMapper;

    @Mock
    private SalePublishPeriodMapper periodMapper;

    @Mock
    private SalePublishSkuMapper publishSkuMapper;

    @Mock
    private PrdProductMapper productMapper;

    @Mock
    private PrdSkuMapper skuMapper;

    @Mock
    private OrdOrderStatusLogMapper orderStatusLogMapper;

    @Mock
    private PayTradeMapper payTradeMapper;

    @Mock
    private UserConvert userConvert;

    @InjectMocks
    private UserOrderServiceImpl userOrderService;

    @Test
    void cancelWaitPayOrder_success() {
        final UserOrderCancelVO request = new UserOrderCancelVO();
        request.setUserId(740001L);
        request.setCancelReason("不想买了");
        final OrdOrder order = new OrdOrder();
        order.setId(761001L);
        order.setUserId(740001L);
        order.setTradeStatus(10L);
        order.setPayStatus(10L);
        final OrderDTO dto = new OrderDTO();
        dto.setId(761001L);
        dto.setTradeStatus(30L);
        dto.setPayStatus(10L);
        when(orderMapper.selectById(761001L)).thenReturn(order);
        when(payTradeMapper.selectList(any())).thenReturn(List.of());
        when(userConvert.toOrderDTO(order)).thenReturn(dto);

        final OrderDTO result = userOrderService.cancelWaitPayOrder(761001L, request);

        assertEquals(30L, order.getTradeStatus());
        assertEquals(10L, order.getPayStatus());
        assertEquals(30L, result.getTradeStatus());
        assertEquals(10L, result.getPayStatus());
        verify(orderMapper).updateById(order);
    }

    @Test
    void cancelWaitPayOrder_paidOrder_throwConflict() {
        final UserOrderCancelVO request = new UserOrderCancelVO();
        request.setUserId(740001L);
        request.setCancelReason("不想买了");
        final OrdOrder order = new OrdOrder();
        order.setId(761002L);
        order.setUserId(740001L);
        order.setTradeStatus(20L);
        order.setPayStatus(30L);
        when(orderMapper.selectById(761002L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> userOrderService.cancelWaitPayOrder(761002L, request));
    }
}
