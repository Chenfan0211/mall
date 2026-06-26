package com.mall.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.product.dto.ProductDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.product.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void pageProducts_success() throws Exception {
        final ProductDTO productDTO = new ProductDTO();
        productDTO.setId(751001L);
        productDTO.setProductName("测试红富士苹果");
        when(productService.pageProducts(any())).thenReturn(PageResult.of(1L, List.of(productDTO)));

        mockMvc.perform(get("/api/product/products")
                        .param("keyword", "苹果")
                        .with(user("tester").authorities(() -> "product:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].productName").value("测试红富士苹果"));
    }

    @Test
    void pageProducts_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/product/products")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "product:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
