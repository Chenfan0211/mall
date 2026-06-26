package com.mall.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.product.dto.SkuDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.product.service.SkuService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SkuController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SkuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkuService skuService;

    @Test
    void listSkus_success() throws Exception {
        final SkuDTO skuDTO = new SkuDTO();
        skuDTO.setId(752001L);
        skuDTO.setSkuName("苹果1kg装");
        when(skuService.listSkus(any())).thenReturn(List.of(skuDTO));

        mockMvc.perform(get("/api/product/skus")
                        .param("productId", "751001")
                        .with(user("tester").authorities(() -> "product:sku:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data[0].skuName").value("苹果1kg装"));
    }
}
