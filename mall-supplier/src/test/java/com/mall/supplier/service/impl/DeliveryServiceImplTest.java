package com.mall.supplier.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.supplier.dto.DeliveryOrderDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.supplier.convert.SupplierConvert;
import com.mall.supplier.entity.PurDeliveryOrder;
import com.mall.supplier.mapper.PurDeliveryItemMapper;
import com.mall.supplier.mapper.PurDeliveryOrderMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private PurDeliveryOrderMapper deliveryOrderMapper;

    @Mock
    private PurDeliveryItemMapper deliveryItemMapper;

    @Mock
    private SupplierConvert supplierConvert;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    @Test
    void pageDeliveryOrders_success() {
        final PurDeliveryOrder order = new PurDeliveryOrder();
        order.setId(771001L);
        order.setDeliveryNo("PDEL_TEST_WAIT");
        final DeliveryOrderDTO orderDTO = new DeliveryOrderDTO();
        orderDTO.setId(771001L);
        orderDTO.setDeliveryNo("PDEL_TEST_WAIT");
        final Page<PurDeliveryOrder> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(order));
        when(deliveryOrderMapper.selectPage(any(), any())).thenReturn(page);
        when(supplierConvert.toDeliveryOrderDTO(order)).thenReturn(orderDTO);

        final PageResult<DeliveryOrderDTO> result = deliveryService.pageDeliveryOrders(null);

        assertEquals(1L, result.getTotal());
        assertEquals("PDEL_TEST_WAIT", result.getList().get(0).getDeliveryNo());
    }

    @Test
    void getDeliveryOrder_notFound_throwNotFound() {
        when(deliveryOrderMapper.selectById(779999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> deliveryService.getDeliveryOrder(779999L));
    }
}
