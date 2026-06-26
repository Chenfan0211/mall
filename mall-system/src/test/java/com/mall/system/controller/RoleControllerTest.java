package com.mall.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.system.dto.RoleDTO;
import com.mall.api.system.vo.RoleQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.system.service.RoleService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Test
    void pageRoles_success() throws Exception {
        final RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(700101L);
        roleDTO.setRoleName("平台管理员");
        when(roleService.pageRoles(any(RoleQueryVO.class))).thenReturn(PageResult.of(1L, List.of(roleDTO)));

        mockMvc.perform(get("/api/system/roles")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .with(user("tester").authorities(() -> "system:role:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].roleName").value("平台管理员"));
    }

    @Test
    void listAccountRoles_success() throws Exception {
        final RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(700101L);
        roleDTO.setRoleName("平台管理员");
        when(roleService.listByAccountId(eq(700001L))).thenReturn(List.of(roleDTO));

        mockMvc.perform(get("/api/system/roles/account/{accountId}", 700001L)
                        .with(user("tester").authorities(() -> "system:role:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data[0].roleName").value("平台管理员"));
    }
}
