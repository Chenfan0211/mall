package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserAfterSaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserAfterSaleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserAfterSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAfterSaleService userAfterSaleService;

    @Test
    void applyAfterSale_success() throws Exception {
        final AfterSaleDTO afterSaleDTO = new AfterSaleDTO();
        afterSaleDTO.setId(767010L);
        afterSaleDTO.setAfterSaleNo("AFS_TEST_USER_APPLY");
        when(userAfterSaleService.applyAfterSale(any())).thenReturn(afterSaleDTO);

        mockMvc.perform(post("/api/user/after-sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001,\"orderItemId\":762005,\"afterSaleType\":1,"
                                + "\"applyQty\":1,\"refundAmount\":19.90,\"applyReason\":\"商品有问题\"}")
                        .with(user("tester").authorities(() -> "user:aftersale:apply")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.afterSaleNo").value("AFS_TEST_USER_APPLY"));
    }

    @Test
    void applyAfterSale_missingReason_returnParamError() throws Exception {
        mockMvc.perform(post("/api/user/after-sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001,\"orderItemId\":762005,\"afterSaleType\":1,"
                                + "\"applyQty\":1,\"refundAmount\":19.90}")
                        .with(user("tester").authorities(() -> "user:aftersale:apply")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
