package com.mall.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.payment.dto.FundFlowDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.payment.service.FundFlowService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FundFlowController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FundFlowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FundFlowService fundFlowService;

    @Test
    void pageFlows_success() throws Exception {
        final FundFlowDTO flowDTO = new FundFlowDTO();
        flowDTO.setId(766101L);
        flowDTO.setFlowNo("FUND_TEST_PAY");
        when(fundFlowService.pageFlows(any())).thenReturn(PageResult.of(1L, List.of(flowDTO)));

        mockMvc.perform(get("/api/payment/fund-flows")
                        .param("flowType", "1")
                        .with(user("tester").authorities(() -> "payment:fund-flow:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].flowNo").value("FUND_TEST_PAY"));
    }

    @Test
    void pageFlows_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/payment/fund-flows")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "payment:fund-flow:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
