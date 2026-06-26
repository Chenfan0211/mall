package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserOrderService;
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

@WebMvcTest(UserOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserOrderService userOrderService;

    @Test
    void pageOrders_success() throws Exception {
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761001L);
        orderDTO.setOrderNo("ORD_TEST_WAIT_PAY");
        when(userOrderService.pageOrders(any())).thenReturn(PageResult.of(1L, List.of(orderDTO)));

        mockMvc.perform(get("/api/user/orders")
                        .param("userId", "740001")
                        .with(user("tester").authorities(() -> "user:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].orderNo").value("ORD_TEST_WAIT_PAY"));
    }

    @Test
    void submitOrder_success() throws Exception {
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761010L);
        orderDTO.setOrderNo("ORD_TEST_SUBMIT");
        when(userOrderService.submitOrder(any())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/user/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001,\"cityId\":440100,\"stationId\":720001,"
                                + "\"pickupName\":\"测试提货人\",\"pickupMobile\":\"13800004101\","
                                + "\"idempotentKey\":\"idem_submit_order\",\"cartItemIds\":[760001]}")
                        .with(user("tester").authorities(() -> "user:order:submit")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.orderNo").value("ORD_TEST_SUBMIT"));
    }

    @Test
    void getOrder_success() throws Exception {
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761001L);
        orderDTO.setUserId(740001L);
        when(userOrderService.getOrder(761001L, 740001L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/user/orders/761001")
                        .param("userId", "740001")
                        .with(user("tester").authorities(() -> "user:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.userId").value("740001"));
    }

    @Test
    void cancelWaitPayOrder_success() throws Exception {
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761001L);
        orderDTO.setTradeStatus(30L);
        when(userOrderService.cancelWaitPayOrder(eq(761001L), any())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/user/orders/761001/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001,\"cancelReason\":\"不想买了\"}")
                        .with(user("tester").authorities(() -> "user:order:cancel")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.tradeStatus").value("30"));
    }

    @Test
    void listFulfillmentTracks_success() throws Exception {
        final FulfillmentTrackDTO trackDTO = new FulfillmentTrackDTO();
        trackDTO.setId(764001L);
        trackDTO.setNodeName("待支付");
        when(userOrderService.listFulfillmentTracks(eq(761001L), eq(740001L))).thenReturn(List.of(trackDTO));

        mockMvc.perform(get("/api/user/orders/761001/tracks")
                        .param("userId", "740001")
                        .with(user("tester").authorities(() -> "user:order:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data[0].nodeName").value("待支付"));
    }

    @Test
    void createPaymentTrade_success() throws Exception {
        final PaymentTradeDTO paymentTradeDTO = new PaymentTradeDTO();
        paymentTradeDTO.setId(765010L);
        paymentTradeDTO.setPayNo("PAY_TEST_USER_CREATE");
        paymentTradeDTO.setPayAmount(new BigDecimal("39.8000"));
        when(userOrderService.createPaymentTrade(eq(761001L), any())).thenReturn(paymentTradeDTO);

        mockMvc.perform(post("/api/user/orders/761001/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001,\"channelType\":1,\"idempotentKey\":\"idem_pay_user\"}")
                        .with(user("tester").authorities(() -> "user:payment:create")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.payNo").value("PAY_TEST_USER_CREATE"));
    }
}
