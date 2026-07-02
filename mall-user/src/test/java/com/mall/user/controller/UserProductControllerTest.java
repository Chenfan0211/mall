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
import com.mall.api.user.dto.UserProductPurchaseRecordDTO;
import com.mall.api.user.dto.UserProductReviewStatsDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserProductService;
import java.math.BigDecimal;
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
        categoryDTO.setImageUrl("https://cdn.example.com/fruit.png");
        when(userProductService.pageCategories(any())).thenReturn(PageResult.of(1L, List.of(categoryDTO)));

        mockMvc.perform(get("/api/user/products/categories")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].categoryName").value("水果鲜食"))
                .andExpect(jsonPath("$.data.list[0].imageUrl").value("https://cdn.example.com/fruit.png"));
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
    void getReviewStats_success() throws Exception {
        final UserProductReviewStatsDTO statsDTO = new UserProductReviewStatsDTO();
        statsDTO.setCommentCount(8L);
        statsDTO.setGoodRatePercent(new BigDecimal("87.5"));
        statsDTO.setImageReviewCount(3L);
        statsDTO.setRecentSoldQty(36L);
        statsDTO.setRecentRepurchaseUserCount(5L);
        statsDTO.setTags(List.of(new UserProductReviewStatsDTO.TagCountDTO("新鲜/色泽好", 6L)));
        when(userProductService.getReviewStats(eq(751001L), any())).thenReturn(statsDTO);

        mockMvc.perform(get("/api/user/products/{id}/review-stats", 751001L)
                        .param("cityId", "440100")
                        .param("stationId", "720001")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.commentCount").value(8))
                .andExpect(jsonPath("$.data.goodRatePercent").value(87.5))
                .andExpect(jsonPath("$.data.imageReviewCount").value(3))
                .andExpect(jsonPath("$.data.recentSoldQty").value(36))
                .andExpect(jsonPath("$.data.recentRepurchaseUserCount").value(5))
                .andExpect(jsonPath("$.data.tags[0].label").value("新鲜/色泽好"))
                .andExpect(jsonPath("$.data.tags[0].count").value(6));
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
                        .param("productId", "610001")
                        .param("hasImage", "true")
                        .param("sortField", "latest")
                        .param("sortOrder", "desc")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].content").value("新鲜好吃"));
    }

    @Test
    void pagePurchaseRecords_success() throws Exception {
        final UserProductPurchaseRecordDTO recordDTO = new UserProductPurchaseRecordDTO();
        recordDTO.setUserName("张*三");
        recordDTO.setProductName("四川爱媛果冻橙");
        recordDTO.setSkuName("5斤装");
        recordDTO.setQty(2L);
        when(userProductService.pagePurchaseRecords(eq(610001L), any()))
                .thenReturn(PageResult.of(1L, List.of(recordDTO)));

        mockMvc.perform(get("/api/user/products/{id}/purchase-records", 610001L)
                        .param("pageNum", "1")
                        .param("pageSize", "3")
                        .param("cityId", "440100")
                        .param("stationId", "720001")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].userName").value("张*三"))
                .andExpect(jsonPath("$.data.list[0].productName").value("四川爱媛果冻橙"))
                .andExpect(jsonPath("$.data.list[0].skuName").value("5斤装"))
                .andExpect(jsonPath("$.data.list[0].qty").value(2));
    }
}
