package com.mall.station.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.station.dto.StationWorkbenchDTO;
import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.station.service.StationOrderService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StationOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StationOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationOrderService stationOrderService;

    @Test
    void getWorkbench_success() throws Exception {
        final StationWorkbenchDTO workbenchDTO = new StationWorkbenchDTO();
        workbenchDTO.setStationId(720001L);
        workbenchDTO.setTodayOrderCount(5L);
        workbenchDTO.setAvailableAmount(new BigDecimal("800.0000"));
        when(stationOrderService.getWorkbench(any())).thenReturn(workbenchDTO);

        mockMvc.perform(get("/api/station/workbench/summary")
                        .param("stationId", "720001")
                        .with(user("tester").authorities(() -> "station:workbench:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.todayOrderCount").value("5"));
    }

    @Test
    void pageOrders_success() throws Exception {
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761005L);
        orderDTO.setOrderNo("ORD_TEST_WAIT_PICKUP");
        when(stationOrderService.pageOrders(any())).thenReturn(PageResult.of(1L, List.of(orderDTO)));

        mockMvc.perform(get("/api/station/workbench/orders")
                        .param("fulfillStatus", "60")
                        .with(user("tester").authorities(() -> "station:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].orderNo").value("ORD_TEST_WAIT_PICKUP"));
    }

    @Test
    void pageOrders_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/station/workbench/orders")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "station:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void confirmPickup_success() throws Exception {
        final OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setId(762005L);
        itemDTO.setFulfillStatus(70L);
        when(stationOrderService.confirmPickup(eq(762005L), any())).thenReturn(itemDTO);

        mockMvc.perform(post("/api/station/workbench/order-items/762005/pickup-confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pickedQty\":1,\"operatorId\":700012}")
                        .with(user("tester").authorities(() -> "station:order:pickup-confirm")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.fulfillStatus").value("70"));
    }

    @Test
    void confirmArrival_success() throws Exception {
        final DeliveryStationDTO stationDTO = new DeliveryStationDTO();
        stationDTO.setId(786102L);
        stationDTO.setStatus(30L);
        when(stationOrderService.confirmArrival(eq(786102L), any())).thenReturn(stationDTO);

        mockMvc.perform(post("/api/station/workbench/delivery-stations/786102/arrival-confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operatorId\":700012,\"remark\":\"交接完成\"}")
                        .with(user("tester").authorities(() -> "station:arrival:confirm")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.status").value("30"));
    }

    @Test
    void confirmPickup_invalidQty_returnParamError() throws Exception {
        mockMvc.perform(post("/api/station/workbench/order-items/762005/pickup-confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pickedQty\":0}")
                        .with(user("tester").authorities(() -> "station:order:pickup-confirm")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void submitException_success() throws Exception {
        final ExceptionRecordDTO dto = new ExceptionRecordDTO();
        dto.setExceptionNo("EX_TEST");
        dto.setTitle("页面覆盖赣南鲜橙 少件1件");
        when(stationOrderService.submitException(any())).thenReturn(dto);

        mockMvc.perform(post("/api/station/workbench/exceptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "stationId":720001,
                                  "productName":"页面覆盖赣南鲜橙",
                                  "skuName":"鲜橙3kg装",
                                  "exceptionType":"少件",
                                  "totalQty":1,
                                  "items":[{"orderItemId":762005,"orderId":761005,"orderNo":"ORD_TEST","qty":1}]
                                }
                                """)
                        .with(user("tester").authorities(() -> "station:exception:create")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.exceptionNo").value("EX_TEST"));
    }
}
