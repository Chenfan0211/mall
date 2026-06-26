package com.mall.aftersale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.aftersale.service.AfterSaleItemService;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
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

@WebMvcTest(AfterSaleItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AfterSaleItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AfterSaleItemService afterSaleItemService;

    @Test
    void pageItems_success() throws Exception {
        final AfterSaleItemDTO itemDTO = new AfterSaleItemDTO();
        itemDTO.setId(767101L);
        itemDTO.setAfterSaleId(767001L);
        when(afterSaleItemService.pageItems(any())).thenReturn(PageResult.of(1L, List.of(itemDTO)));

        mockMvc.perform(get("/api/aftersale/items")
                        .param("afterSaleId", "767001")
                        .with(user("tester").authorities(() -> "aftersale:item:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].afterSaleId").value(767001));
    }

    @Test
    void pageItems_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/aftersale/items")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "aftersale:item:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
