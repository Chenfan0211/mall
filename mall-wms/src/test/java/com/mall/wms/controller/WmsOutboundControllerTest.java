package com.mall.wms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.WaveBatchDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.wms.service.WmsOutboundService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WmsOutboundController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class WmsOutboundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WmsOutboundService wmsOutboundService;

    @Test
    void pageWaveBatches_success() throws Exception {
        final WaveBatchDTO waveBatchDTO = new WaveBatchDTO();
        waveBatchDTO.setId(784001L);
        waveBatchDTO.setWaveNo("WAVE_TEST_WAIT_LOCK");
        when(wmsOutboundService.pageWaveBatches(any())).thenReturn(PageResult.of(1L, List.of(waveBatchDTO)));

        mockMvc.perform(get("/api/wms/outbound/waves")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "wms:wave:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].waveNo").value("WAVE_TEST_WAIT_LOCK"));
    }

    @Test
    void getDeliveryOrder_success() throws Exception {
        final DeliveryOrderDTO deliveryOrderDTO = new DeliveryOrderDTO();
        deliveryOrderDTO.setId(786001L);
        deliveryOrderDTO.setDeliveryNo("WMS_DELIVERY_TEST_WAIT");
        when(wmsOutboundService.getDeliveryOrder(786001L)).thenReturn(deliveryOrderDTO);

        mockMvc.perform(get("/api/wms/outbound/deliveries/786001")
                        .with(user("tester").authorities(() -> "wms:delivery:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.deliveryNo").value("WMS_DELIVERY_TEST_WAIT"));
    }

    @Test
    void pageWaveBatches_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/wms/outbound/waves")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "wms:wave:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
