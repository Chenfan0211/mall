package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.user.dto.UserHomeDTO;
import com.mall.api.user.dto.UserHomeAssetDTO;
import com.mall.api.user.dto.UserHomeBannerDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserHomeService;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserHomeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserHomeService userHomeService;

    @Test
    void getHome_success() throws Exception {
        final UserHomeDTO homeDTO = new UserHomeDTO();
        homeDTO.setUserId(740001L);
        homeDTO.setOnlineProductCount(8L);
        when(userHomeService.getHome(any())).thenReturn(homeDTO);

        mockMvc.perform(get("/api/user/home/summary")
                .param("userId", "740001")
                .with(user("tester").authorities(() -> "user:home:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.onlineProductCount").value(8));
    }

    @Test
    void getHomeAssets_success() throws Exception {
        final UserHomeBannerDTO bannerDTO = new UserHomeBannerDTO();
        bannerDTO.setCode("HOME_MAIN");
        bannerDTO.setActivityCode("HOME_MAIN");
        bannerDTO.setImageUrl("https://cdn.example.com/banner.png");
        bannerDTO.setLinkUrl("/pages/activity/index?activityCode=HOME_MAIN");
        final UserHomeAssetDTO assetDTO = new UserHomeAssetDTO();
        assetDTO.setBanners(List.of(bannerDTO));
        when(userHomeService.getHomeAssets(any())).thenReturn(assetDTO);

        mockMvc.perform(get("/api/user/home/assets")
                .param("cityId", "440100")
                .param("stationId", "720001")
                .with(user("tester").authorities(() -> "user:home:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.banners[0].imageUrl").value("https://cdn.example.com/banner.png"))
                .andExpect(jsonPath("$.data.banners[0].activityCode").value("HOME_MAIN"))
                .andExpect(jsonPath("$.data.banners[0].linkUrl").value("/pages/activity/index?activityCode=HOME_MAIN"));
    }

    @Test
    void getHome_mobileEndpoint_hasNoPreAuthorize() throws Exception {
        final Method method = UserHomeController.class.getDeclaredMethod("getHome", com.mall.api.user.vo.UserHomeQueryVO.class);

        assertNull(method.getAnnotation(PreAuthorize.class));
    }

    @Test
    void getHomeAssets_mobileEndpoint_hasNoPreAuthorize() throws Exception {
        final Method method = UserHomeController.class.getDeclaredMethod("getHomeAssets", com.mall.api.user.vo.UserHomeAssetQueryVO.class);

        assertNull(method.getAnnotation(PreAuthorize.class));
    }
}
