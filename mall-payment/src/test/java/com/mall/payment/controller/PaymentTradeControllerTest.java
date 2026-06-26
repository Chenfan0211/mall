package com.mall.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.payment.service.PaymentTradeService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentTradeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PaymentTradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentTradeService paymentTradeService;

    @Test
    void pageTrades_success() throws Exception {
        final PaymentTradeDTO tradeDTO = new PaymentTradeDTO();
        tradeDTO.setId(765001L);
        tradeDTO.setPayNo("PAY_TEST_WAIT");
        when(paymentTradeService.pageTrades(any())).thenReturn(PageResult.of(1L, List.of(tradeDTO)));

        mockMvc.perform(get("/api/payment/trades")
                        .param("tradeStatus", "10")
                        .with(user("tester").authorities(() -> "payment:trade:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].payNo").value("PAY_TEST_WAIT"));
    }

    @Test
    void pageTrades_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/payment/trades")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "payment:trade:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
