package com.mall.aftersale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.aftersale.service.AfterSaleService;
import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AfterSaleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AfterSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AfterSaleService afterSaleService;

    @Test
    void pageAfterSales_success() throws Exception {
        final AfterSaleDTO afterSaleDTO = new AfterSaleDTO();
        afterSaleDTO.setId(767001L);
        afterSaleDTO.setAfterSaleNo("AFS_TEST_WAIT_AUDIT");
        when(afterSaleService.pageAfterSales(any())).thenReturn(PageResult.of(1L, List.of(afterSaleDTO)));

        mockMvc.perform(get("/api/aftersale/after-sales")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "aftersale:after-sale:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].afterSaleNo").value("AFS_TEST_WAIT_AUDIT"));
    }

    @Test
    void pageAfterSales_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/aftersale/after-sales")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "aftersale:after-sale:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
