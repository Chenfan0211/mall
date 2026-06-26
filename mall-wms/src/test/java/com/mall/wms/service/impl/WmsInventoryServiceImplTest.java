package com.mall.wms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.wms.dto.InventoryDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsInventory;
import com.mall.wms.mapper.WmsInventoryLockMapper;
import com.mall.wms.mapper.WmsInventoryLogMapper;
import com.mall.wms.mapper.WmsInventoryMapper;
import com.mall.wms.mapper.WmsStocktakeOrderMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WmsInventoryServiceImplTest {

    @Mock
    private WmsInventoryMapper inventoryMapper;

    @Mock
    private WmsInventoryLockMapper inventoryLockMapper;

    @Mock
    private WmsInventoryLogMapper inventoryLogMapper;

    @Mock
    private WmsStocktakeOrderMapper stocktakeOrderMapper;

    @Mock
    private WmsConvert wmsConvert;

    @InjectMocks
    private WmsInventoryServiceImpl wmsInventoryService;

    @Test
    void pageInventories_success() {
        final WmsInventory inventory = new WmsInventory();
        inventory.setId(783002L);
        inventory.setBatchNo("BATCH_INV_SALE");
        final InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(783002L);
        inventoryDTO.setBatchNo("BATCH_INV_SALE");
        final Page<WmsInventory> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(inventory));
        when(inventoryMapper.selectPage(any(), any())).thenReturn(page);
        when(wmsConvert.toInventoryDTO(inventory)).thenReturn(inventoryDTO);

        final PageResult<InventoryDTO> result = wmsInventoryService.pageInventories(null);

        assertEquals(1L, result.getTotal());
        assertEquals("BATCH_INV_SALE", result.getList().get(0).getBatchNo());
    }

    @Test
    void getInventory_notFound_throwNotFound() {
        when(inventoryMapper.selectById(783999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> wmsInventoryService.getInventory(783999L));
    }
}
