package com.mall.trade.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdOrder;
import com.mall.trade.mapper.OrdOrderMapper;
import com.mall.trade.mapper.OrdOrderStatusLogMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrdOrderMapper orderMapper;

    @Mock
    private OrdOrderStatusLogMapper statusLogMapper;

    @Mock
    private TradeConvert tradeConvert;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void pageOrders_success() {
        final OrdOrder order = new OrdOrder();
        order.setId(761001L);
        order.setOrderNo("ORD_TEST_WAIT_PAY");
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761001L);
        orderDTO.setOrderNo("ORD_TEST_WAIT_PAY");
        final Page<OrdOrder> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(order));
        when(orderMapper.selectPage(any(), any())).thenReturn(page);
        when(tradeConvert.toOrderDTO(order)).thenReturn(orderDTO);

        final PageResult<OrderDTO> result = orderService.pageOrders(null);

        assertEquals(1L, result.getTotal());
        assertEquals("ORD_TEST_WAIT_PAY", result.getList().get(0).getOrderNo());
    }

    @Test
    void getOrder_notFound_throwNotFound() {
        when(orderMapper.selectById(761999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> orderService.getOrder(761999L));
    }
}
