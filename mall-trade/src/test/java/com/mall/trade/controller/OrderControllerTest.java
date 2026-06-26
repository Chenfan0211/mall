package com.mall.trade.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.trade.dto.OrderDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.trade.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void pageOrders_success() throws Exception {
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761001L);
        orderDTO.setOrderNo("ORD_TEST_WAIT_PAY");
        when(orderService.pageOrders(any())).thenReturn(PageResult.of(1L, List.of(orderDTO)));

        mockMvc.perform(get("/api/trade/orders")
                        .param("tradeStatus", "10")
                        .with(user("tester").authorities(() -> "trade:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].orderNo").value("ORD_TEST_WAIT_PAY"));
    }

    @Test
    void pageOrders_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/trade/orders")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "trade:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
