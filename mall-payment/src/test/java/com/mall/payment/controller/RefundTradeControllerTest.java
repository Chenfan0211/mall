package com.mall.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.payment.dto.RefundTradeDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.payment.service.RefundTradeService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RefundTradeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RefundTradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefundTradeService refundTradeService;

    @Test
    void pageRefunds_success() throws Exception {
        final RefundTradeDTO refundDTO = new RefundTradeDTO();
        refundDTO.setId(766001L);
        refundDTO.setRefundNo("RF_TEST_WAIT");
        when(refundTradeService.pageRefunds(any())).thenReturn(PageResult.of(1L, List.of(refundDTO)));

        mockMvc.perform(get("/api/payment/refunds")
                        .param("refundStatus", "10")
                        .with(user("tester").authorities(() -> "payment:refund:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].refundNo").value("RF_TEST_WAIT"));
    }

    @Test
    void pageRefunds_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/payment/refunds")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "payment:refund:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
