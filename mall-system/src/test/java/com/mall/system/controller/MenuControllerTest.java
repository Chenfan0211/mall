package com.mall.system.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.system.dto.MenuDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.system.service.MenuService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    @Test
    void listVisibleMenus_success() throws Exception {
        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(810001L);
        menuDTO.setMenuName("运营驾驶舱");
        when(menuService.listVisibleMenus(anyString())).thenReturn(List.of(menuDTO));

        mockMvc.perform(get("/api/system/menus")
                        .param("portalCode", "admin")
                        .with(user("tester").authorities(() -> "system:menu:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data[0].menuName").value("运营驾驶舱"));
    }
}
