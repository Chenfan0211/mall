package com.mall.wms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.wms.dto.InboundOrderDTO;
import com.mall.api.wms.dto.PutawayTaskDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.wms.service.WmsInboundService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WmsInboundController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class WmsInboundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WmsInboundService wmsInboundService;

    @Test
    void pageInboundOrders_success() throws Exception {
        final InboundOrderDTO inboundOrderDTO = new InboundOrderDTO();
        inboundOrderDTO.setId(781001L);
        inboundOrderDTO.setInboundNo("INB_TEST_WAIT_ARRIVAL");
        when(wmsInboundService.pageInboundOrders(any())).thenReturn(PageResult.of(1L, List.of(inboundOrderDTO)));

        mockMvc.perform(get("/api/wms/inbound/orders")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "wms:inbound:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].inboundNo").value("INB_TEST_WAIT_ARRIVAL"));
    }

    @Test
    void getPutawayTask_success() throws Exception {
        final PutawayTaskDTO putawayTaskDTO = new PutawayTaskDTO();
        putawayTaskDTO.setId(782001L);
        putawayTaskDTO.setPutawayNo("PUT_TEST_WAIT");
        when(wmsInboundService.getPutawayTask(782001L)).thenReturn(putawayTaskDTO);

        mockMvc.perform(get("/api/wms/inbound/putaway-tasks/782001")
                        .with(user("tester").authorities(() -> "wms:putaway:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.putawayNo").value("PUT_TEST_WAIT"));
    }

    @Test
    void pageInboundOrders_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/wms/inbound/orders")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "wms:inbound:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
