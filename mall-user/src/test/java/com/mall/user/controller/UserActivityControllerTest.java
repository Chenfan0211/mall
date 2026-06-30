package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.user.dto.UserProductCardDTO;
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

@WebMvcTest(UserActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProductService userProductService;

    @Test
    void pageActivityProducts_success() throws Exception {
        final UserProductCardDTO cardDTO = new UserProductCardDTO();
        cardDTO.setProductId(751001L);
        cardDTO.setProductName("活动商品");
        when(userProductService.pageActivityProducts(eq("HOME_MAIN"), any()))
                .thenReturn(PageResult.of(1L, List.of(cardDTO)));

        mockMvc.perform(get("/api/user/activities/{activityCode}/products", "HOME_MAIN")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .param("cityId", "440100")
                        .param("stationId", "720001")
                        .with(user("tester").authorities(() -> "user:product:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].productName").value("活动商品"));
    }
}
