package com.mall.trade.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.trade.dto.StockoutRecordDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.trade.service.StockoutRecordService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockoutRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StockoutRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockoutRecordService stockoutRecordService;

    @Test
    void pageStockouts_success() throws Exception {
        final StockoutRecordDTO recordDTO = new StockoutRecordDTO();
        recordDTO.setId(784301L);
        recordDTO.setStockoutNo("STOCKOUT_TEST_WAIT");
        when(stockoutRecordService.pageStockouts(any())).thenReturn(PageResult.of(1L, List.of(recordDTO)));

        mockMvc.perform(get("/api/trade/stockouts")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "trade:stockout:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].stockoutNo").value("STOCKOUT_TEST_WAIT"));
    }

    @Test
    void pageStockouts_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/trade/stockouts")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "trade:stockout:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
