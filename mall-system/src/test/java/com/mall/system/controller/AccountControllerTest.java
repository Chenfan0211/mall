package com.mall.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.system.dto.AccountDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.system.service.AccountService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void pageAccounts_success() throws Exception {
        final AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(700001L);
        accountDTO.setUsername("test_platform_admin");
        accountDTO.setLoginPortalCode("admin");
        when(accountService.pageAccounts(any())).thenReturn(PageResult.of(1L, List.of(accountDTO)));

        mockMvc.perform(get("/api/system/accounts")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .with(user("tester").authorities(() -> "system:account:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value("test_platform_admin"));
    }

    @Test
    void getAccount_success() throws Exception {
        final AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(700001L);
        accountDTO.setUsername("test_platform_admin");
        when(accountService.getAccount(700001L)).thenReturn(accountDTO);

        mockMvc.perform(get("/api/system/accounts/{id}", 700001L)
                        .with(user("tester").authorities(() -> "system:account:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.id").value(700001))
                .andExpect(jsonPath("$.data.username").value("test_platform_admin"));
    }

    @Test
    void pageAccounts_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/system/accounts")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "system:account:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
