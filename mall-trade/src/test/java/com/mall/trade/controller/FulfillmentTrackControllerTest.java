package com.mall.trade.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.trade.service.FulfillmentTrackService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FulfillmentTrackController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FulfillmentTrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FulfillmentTrackService fulfillmentTrackService;

    @Test
    void listTracks_success() throws Exception {
        final FulfillmentTrackDTO trackDTO = new FulfillmentTrackDTO();
        trackDTO.setId(764003L);
        trackDTO.setNodeName("待自提");
        when(fulfillmentTrackService.listTracks(any())).thenReturn(List.of(trackDTO));

        mockMvc.perform(get("/api/trade/fulfillment-tracks")
                        .param("orderId", "761005")
                        .with(user("tester").authorities(() -> "trade:fulfillment:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data[0].nodeName").value("待自提"));
    }
}
