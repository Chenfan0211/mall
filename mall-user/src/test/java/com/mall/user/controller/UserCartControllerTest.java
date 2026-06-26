package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.user.dto.UserCartItemDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserCartService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserCartController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCartService userCartService;

    @Test
    void pageCartItems_success() throws Exception {
        final UserCartItemDTO itemDTO = new UserCartItemDTO();
        itemDTO.setId(760001L);
        itemDTO.setProductName("测试红富士苹果");
        when(userCartService.pageCartItems(any())).thenReturn(PageResult.of(1L, List.of(itemDTO)));

        mockMvc.perform(get("/api/user/cart/items")
                        .param("userId", "740001")
                        .with(user("tester").authorities(() -> "user:cart:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].productName").value("测试红富士苹果"));
    }

    @Test
    void addCartItem_success() throws Exception {
        final UserCartItemDTO itemDTO = new UserCartItemDTO();
        itemDTO.setId(760001L);
        itemDTO.setQty(3L);
        when(userCartService.addCartItem(any())).thenReturn(itemDTO);

        mockMvc.perform(post("/api/user/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001,\"cityId\":440100,\"stationId\":720001,"
                                + "\"periodId\":755002,\"productId\":751001,\"skuId\":752001,\"qty\":1}")
                        .with(user("tester").authorities(() -> "user:cart:add")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.qty").value("3"));
    }

    @Test
    void addCartItem_invalidQty_returnParamError() throws Exception {
        mockMvc.perform(post("/api/user/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001,\"cityId\":440100,\"stationId\":720001,"
                                + "\"periodId\":755002,\"productId\":751001,\"skuId\":752001,\"qty\":0}")
                        .with(user("tester").authorities(() -> "user:cart:add")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void removeCartItem_success() throws Exception {
        final UserCartItemDTO itemDTO = new UserCartItemDTO();
        itemDTO.setId(760001L);
        itemDTO.setValidStatus(2L);
        when(userCartService.removeCartItem(eq(760001L), any())).thenReturn(itemDTO);

        mockMvc.perform(delete("/api/user/cart/items/760001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":740001}")
                        .with(user("tester").authorities(() -> "user:cart:remove")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.validStatus").value("2"));
    }
}
