package com.mall.supplier.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.supplier.dto.PurchaseOrderDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.supplier.convert.SupplierConvert;
import com.mall.supplier.entity.PurPurchaseOrder;
import com.mall.supplier.mapper.PurPurchaseAuditLogMapper;
import com.mall.supplier.mapper.PurPurchaseItemMapper;
import com.mall.supplier.mapper.PurPurchaseOrderMapper;
import com.mall.supplier.mapper.PurPurchaseSourceLogMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurPurchaseOrderMapper purchaseOrderMapper;

    @Mock
    private PurPurchaseItemMapper purchaseItemMapper;

    @Mock
    private PurPurchaseAuditLogMapper auditLogMapper;

    @Mock
    private PurPurchaseSourceLogMapper sourceLogMapper;

    @Mock
    private SupplierConvert supplierConvert;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    @Test
    void pagePurchaseOrders_success() {
        final PurPurchaseOrder order = new PurPurchaseOrder();
        order.setId(770001L);
        order.setPurchaseNo("PUR_TEST_WAIT_AUDIT");
        final PurchaseOrderDTO orderDTO = new PurchaseOrderDTO();
        orderDTO.setId(770001L);
        orderDTO.setPurchaseNo("PUR_TEST_WAIT_AUDIT");
        final Page<PurPurchaseOrder> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(order));
        when(purchaseOrderMapper.selectPage(any(), any())).thenReturn(page);
        when(supplierConvert.toPurchaseOrderDTO(order)).thenReturn(orderDTO);

        final PageResult<PurchaseOrderDTO> result = purchaseService.pagePurchaseOrders(null);

        assertEquals(1L, result.getTotal());
        assertEquals("PUR_TEST_WAIT_AUDIT", result.getList().get(0).getPurchaseNo());
    }

    @Test
    void getPurchaseOrder_notFound_throwNotFound() {
        when(purchaseOrderMapper.selectById(779999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> purchaseService.getPurchaseOrder(779999L));
    }
}
