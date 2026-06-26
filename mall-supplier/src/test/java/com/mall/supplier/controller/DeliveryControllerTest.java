package com.mall.supplier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.supplier.dto.DeliveryOrderDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.supplier.service.DeliveryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @Test
    void pageDeliveryOrders_success() throws Exception {
        final DeliveryOrderDTO orderDTO = new DeliveryOrderDTO();
        orderDTO.setId(771001L);
        orderDTO.setDeliveryNo("PDEL_TEST_WAIT");
        when(deliveryService.pageDeliveryOrders(any())).thenReturn(PageResult.of(1L, List.of(orderDTO)));

        mockMvc.perform(get("/api/supplier/deliveries")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "delivery:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].deliveryNo").value("PDEL_TEST_WAIT"));
    }

    @Test
    void pageDeliveryOrders_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/supplier/deliveries")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "delivery:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
