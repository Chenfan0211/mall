package com.mall.wms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.wms.dto.InventoryDTO;
import com.mall.api.wms.dto.InventoryLockDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.wms.service.WmsInventoryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WmsInventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class WmsInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WmsInventoryService wmsInventoryService;

    @Test
    void pageInventories_success() throws Exception {
        final InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(783002L);
        inventoryDTO.setBatchNo("BATCH_INV_SALE");
        when(wmsInventoryService.pageInventories(any())).thenReturn(PageResult.of(1L, List.of(inventoryDTO)));

        mockMvc.perform(get("/api/wms/inventory/stocks")
                        .param("inventoryStatus", "20")
                        .with(user("tester").authorities(() -> "wms:inventory:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].batchNo").value("BATCH_INV_SALE"));
    }

    @Test
    void getInventoryLock_success() throws Exception {
        final InventoryLockDTO lockDTO = new InventoryLockDTO();
        lockDTO.setId(783101L);
        lockDTO.setLockNo("LOCK_TEST_ACTIVE");
        when(wmsInventoryService.getInventoryLock(783101L)).thenReturn(lockDTO);

        mockMvc.perform(get("/api/wms/inventory/locks/783101")
                        .with(user("tester").authorities(() -> "wms:inventory-lock:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.lockNo").value("LOCK_TEST_ACTIVE"));
    }

    @Test
    void pageInventories_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/wms/inventory/stocks")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "wms:inventory:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
