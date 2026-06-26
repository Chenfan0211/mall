package com.mall.sale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.sale.dto.PublishSkuDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.sale.service.PublishSkuService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PublishSkuController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PublishSkuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublishSkuService publishSkuService;

    @Test
    void listPublishSkus_success() throws Exception {
        final PublishSkuDTO publishSkuDTO = new PublishSkuDTO();
        publishSkuDTO.setId(756001L);
        publishSkuDTO.setSalePrice(new BigDecimal("19.9000"));
        when(publishSkuService.listPublishSkus(any())).thenReturn(List.of(publishSkuDTO));

        mockMvc.perform(get("/api/sale/publish-skus")
                        .param("periodId", "755002")
                        .with(user("tester").authorities(() -> "sale:period:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data[0].salePrice").value(19.9000));
    }
}
