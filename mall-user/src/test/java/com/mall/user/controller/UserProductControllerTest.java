package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserProductService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProductService userProductService;

    @Test
    void pageCategories_success() throws Exception {
        final CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(750001L);
        categoryDTO.setCategoryName("水果鲜食");
        when(userProductService.pageCategories(any())).thenReturn(PageResult.of(1L, List.of(categoryDTO)));

        mockMvc.perform(get("/api/user/products/categories")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].categoryName").value("水果鲜食"));
    }

    @Test
    void pageProducts_success() throws Exception {
        final UserProductCardDTO cardDTO = new UserProductCardDTO();
        cardDTO.setProductId(751001L);
        cardDTO.setProductName("测试红富士苹果");
        when(userProductService.pageProducts(any())).thenReturn(PageResult.of(1L, List.of(cardDTO)));

        mockMvc.perform(get("/api/user/products")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].productName").value("测试红富士苹果"));
    }

    @Test
    void getProductDetail_success() throws Exception {
        final UserProductDetailDTO detailDTO = new UserProductDetailDTO();
        final ProductDTO productDTO = new ProductDTO();
        productDTO.setId(751001L);
        productDTO.setProductName("测试红富士苹果");
        detailDTO.setProduct(productDTO);
        when(userProductService.getProductDetail(eq(751001L), any())).thenReturn(detailDTO);

        mockMvc.perform(get("/api/user/products/{id}", 751001L)
                        .param("userId", "740001")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.product.productName").value("测试红富士苹果"));
    }

    @Test
    void pageComments_success() throws Exception {
        final UserCommentDTO commentDTO = new UserCommentDTO();
        commentDTO.setId(772001L);
        commentDTO.setContent("新鲜好吃");
        when(userProductService.pageComments(any())).thenReturn(PageResult.of(1L, List.of(commentDTO)));

        mockMvc.perform(get("/api/user/products/comments")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].content").value("新鲜好吃"));
    }
}
