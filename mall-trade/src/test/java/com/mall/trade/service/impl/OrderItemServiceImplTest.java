package com.mall.trade.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdOrderItem;
import com.mall.trade.mapper.OrdOrderItemMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrdOrderItemMapper orderItemMapper;

    @Mock
    private TradeConvert tradeConvert;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    void pageOrderItems_success() {
        final OrdOrderItem orderItem = new OrdOrderItem();
        orderItem.setId(762001L);
        orderItem.setOrderItemNo("ITEM_TEST_WAIT_PAY");
        final OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(762001L);
        orderItemDTO.setOrderItemNo("ITEM_TEST_WAIT_PAY");
        final Page<OrdOrderItem> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(orderItem));
        when(orderItemMapper.selectPage(any(), any())).thenReturn(page);
        when(tradeConvert.toOrderItemDTO(orderItem)).thenReturn(orderItemDTO);

        final PageResult<OrderItemDTO> result = orderItemService.pageOrderItems(null);

        assertEquals(1L, result.getTotal());
        assertEquals("ITEM_TEST_WAIT_PAY", result.getList().get(0).getOrderItemNo());
    }

    @Test
    void getOrderItem_notFound_throwNotFound() {
        when(orderItemMapper.selectById(762999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> orderItemService.getOrderItem(762999L));
    }
}
