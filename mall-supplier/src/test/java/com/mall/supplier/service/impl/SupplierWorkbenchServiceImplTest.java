package com.mall.supplier.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.supplier.dto.SupplierWorkbenchDTO;
import com.mall.api.supplier.vo.SupplierWorkbenchQueryVO;
import com.mall.supplier.entity.PurDeliveryItem;
import com.mall.supplier.entity.PurPurchaseItem;
import com.mall.supplier.entity.PurPurchaseOrder;
import com.mall.supplier.mapper.PurDeliveryItemMapper;
import com.mall.supplier.mapper.PurDeliveryOrderMapper;
import com.mall.supplier.mapper.PurPurchaseItemMapper;
import com.mall.supplier.mapper.PurPurchaseOrderMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierWorkbenchServiceImplTest {

    @Mock
    private PurPurchaseOrderMapper purchaseOrderMapper;

    @Mock
    private PurPurchaseItemMapper purchaseItemMapper;

    @Mock
    private PurDeliveryOrderMapper deliveryOrderMapper;

    @Mock
    private PurDeliveryItemMapper deliveryItemMapper;

    @InjectMocks
    private SupplierWorkbenchServiceImpl supplierWorkbenchService;

    @Test
    void getWorkbench_success() {
        final SupplierWorkbenchQueryVO query = new SupplierWorkbenchQueryVO();
        query.setSupplierId(710001L);
        query.setCityId(440100L);
        final PurPurchaseOrder purchaseOrder = new PurPurchaseOrder();
        purchaseOrder.setId(770001L);
        final PurPurchaseItem purchaseItem = new PurPurchaseItem();
        purchaseItem.setId(770101L);
        purchaseItem.setPurchaseQty(100L);
        purchaseItem.setReceivedQty(80L);
        final PurDeliveryItem deliveryItem = new PurDeliveryItem();
        deliveryItem.setDiffQty(5L);
        when(purchaseOrderMapper.selectCount(any())).thenReturn(1L, 2L);
        when(purchaseOrderMapper.selectList(any())).thenReturn(List.of(purchaseOrder));
        when(deliveryOrderMapper.selectCount(any())).thenReturn(3L, 4L, 5L);
        when(purchaseItemMapper.selectList(any())).thenReturn(List.of(purchaseItem));
        when(deliveryItemMapper.selectList(any())).thenReturn(List.of(deliveryItem));

        final SupplierWorkbenchDTO result = supplierWorkbenchService.getWorkbench(query);

        assertEquals(710001L, result.getSupplierId());
        assertEquals(1L, result.getWaitAuditPurchaseCount());
        assertEquals(3L, result.getTodayDeliveryOrderCount());
        assertEquals(100L, result.getPurchaseQty());
        assertEquals(5L, result.getDiffQty());
    }
}
