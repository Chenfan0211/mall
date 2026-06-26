package com.mall.supplier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.supplier.dto.SupplierWorkbenchDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.supplier.service.SupplierWorkbenchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierWorkbenchController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SupplierWorkbenchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierWorkbenchService supplierWorkbenchService;

    @Test
    void getWorkbench_success() throws Exception {
        final SupplierWorkbenchDTO workbenchDTO = new SupplierWorkbenchDTO();
        workbenchDTO.setSupplierId(710001L);
        workbenchDTO.setWaitDeliveryPurchaseCount(2L);
        when(supplierWorkbenchService.getWorkbench(any())).thenReturn(workbenchDTO);

        mockMvc.perform(get("/api/supplier/workbench/summary")
                        .param("supplierId", "710001")
                        .with(user("tester").authorities(() -> "supplier:workbench:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.waitDeliveryPurchaseCount").value("2"));
    }
}
