package com.mall.supplier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.supplier.dto.SupplierDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.supplier.service.SupplierProfileService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SupplierProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierProfileService supplierProfileService;

    @Test
    void pageSuppliers_success() throws Exception {
        final SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setId(710001L);
        supplierDTO.setSupplierNo("SUP_TEST_FRESH");
        when(supplierProfileService.pageSuppliers(any())).thenReturn(PageResult.of(1L, List.of(supplierDTO)));

        mockMvc.perform(get("/api/supplier/profiles")
                        .param("status", "1")
                        .with(user("tester").authorities(() -> "supplier:profile:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].supplierNo").value("SUP_TEST_FRESH"));
    }

    @Test
    void pageSuppliers_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/supplier/profiles")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "supplier:profile:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
