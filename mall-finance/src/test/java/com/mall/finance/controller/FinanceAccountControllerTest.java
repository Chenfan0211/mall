package com.mall.finance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.finance.service.FinanceAccountService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FinanceAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FinanceAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinanceAccountService financeAccountService;

    @Test
    void pageAccounts_success() throws Exception {
        final FinanceAccountDTO accountDTO = new FinanceAccountDTO();
        accountDTO.setId(790001L);
        accountDTO.setAccountNo("FIN_ACC_SUPPLIER");
        when(financeAccountService.pageAccounts(any())).thenReturn(PageResult.of(1L, List.of(accountDTO)));

        mockMvc.perform(get("/api/finance/accounts")
                        .param("status", "1")
                        .with(user("tester").authorities(() -> "finance:account:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].accountNo").value("FIN_ACC_SUPPLIER"));
    }

    @Test
    void getAccount_success() throws Exception {
        final FinanceAccountDTO accountDTO = new FinanceAccountDTO();
        accountDTO.setId(790001L);
        accountDTO.setAccountNo("FIN_ACC_SUPPLIER");
        when(financeAccountService.getAccount(790001L)).thenReturn(accountDTO);

        mockMvc.perform(get("/api/finance/accounts/790001")
                        .with(user("tester").authorities(() -> "finance:account:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.accountNo").value("FIN_ACC_SUPPLIER"));
    }

    @Test
    void pageAccountFlows_success() throws Exception {
        final FinanceAccountFlowDTO flowDTO = new FinanceAccountFlowDTO();
        flowDTO.setId(790101L);
        flowDTO.setFlowNo("ACC_FLOW_COMMISSION");
        when(financeAccountService.pageAccountFlows(any())).thenReturn(PageResult.of(1L, List.of(flowDTO)));

        mockMvc.perform(get("/api/finance/accounts/flows")
                        .param("flowType", "1")
                        .with(user("tester").authorities(() -> "finance:account-flow:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].flowNo").value("ACC_FLOW_COMMISSION"));
    }

    @Test
    void getAccountFlow_success() throws Exception {
        final FinanceAccountFlowDTO flowDTO = new FinanceAccountFlowDTO();
        flowDTO.setId(790101L);
        flowDTO.setFlowNo("ACC_FLOW_COMMISSION");
        when(financeAccountService.getAccountFlow(790101L)).thenReturn(flowDTO);

        mockMvc.perform(get("/api/finance/accounts/flows/790101")
                        .with(user("tester").authorities(() -> "finance:account-flow:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.flowNo").value("ACC_FLOW_COMMISSION"));
    }

    @Test
    void pageAccounts_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/accounts")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:account:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }

    @Test
    void pageAccountFlows_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/accounts/flows")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:account-flow:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
