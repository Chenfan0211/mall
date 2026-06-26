package com.mall.wms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.wms.dto.WarehouseDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.wms.service.WmsBaseService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WmsBaseController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class WmsBaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WmsBaseService wmsBaseService;

    @Test
    void pageWarehouses_success() throws Exception {
        final WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(50001L);
        warehouseDTO.setWarehouseCode("WH_GZ_CENTER");
        when(wmsBaseService.pageWarehouses(any())).thenReturn(PageResult.of(1L, List.of(warehouseDTO)));

        mockMvc.perform(get("/api/wms/base/warehouses")
                        .param("status", "1")
                        .with(user("tester").authorities(() -> "wms:warehouse:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].warehouseCode").value("WH_GZ_CENTER"));
    }

    @Test
    void getWarehouse_success() throws Exception {
        final WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(50001L);
        warehouseDTO.setWarehouseCode("WH_GZ_CENTER");
        when(wmsBaseService.getWarehouse(50001L)).thenReturn(warehouseDTO);

        mockMvc.perform(get("/api/wms/base/warehouses/50001")
                        .with(user("tester").authorities(() -> "wms:warehouse:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.warehouseCode").value("WH_GZ_CENTER"));
    }

    @Test
    void pageWarehouses_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/wms/base/warehouses")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "wms:warehouse:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
