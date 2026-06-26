package com.mall.finance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.finance.dto.DepositRecordDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.finance.service.WithdrawService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WithdrawController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class WithdrawControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WithdrawService withdrawService;

    @Test
    void pageWithdrawApplies_success() throws Exception {
        final WithdrawApplyDTO withdrawDTO = new WithdrawApplyDTO();
        withdrawDTO.setId(790401L);
        withdrawDTO.setWithdrawNo("WD_TEST_WAIT");
        when(withdrawService.pageWithdrawApplies(any())).thenReturn(PageResult.of(1L, List.of(withdrawDTO)));

        mockMvc.perform(get("/api/finance/withdraws")
                        .param("auditStatus", "10")
                        .with(user("tester").authorities(() -> "finance:withdraw:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].withdrawNo").value("WD_TEST_WAIT"));
    }

    @Test
    void getWithdrawApply_success() throws Exception {
        final WithdrawApplyDTO withdrawDTO = new WithdrawApplyDTO();
        withdrawDTO.setId(790401L);
        withdrawDTO.setWithdrawNo("WD_TEST_WAIT");
        when(withdrawService.getWithdrawApply(790401L)).thenReturn(withdrawDTO);

        mockMvc.perform(get("/api/finance/withdraws/790401")
                        .with(user("tester").authorities(() -> "finance:withdraw:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.withdrawNo").value("WD_TEST_WAIT"));
    }

    @Test
    void pageDepositRecords_success() throws Exception {
        final DepositRecordDTO depositDTO = new DepositRecordDTO();
        depositDTO.setId(790501L);
        depositDTO.setDepositNo("DEP_TEST_FROZEN");
        when(withdrawService.pageDepositRecords(any())).thenReturn(PageResult.of(1L, List.of(depositDTO)));

        mockMvc.perform(get("/api/finance/withdraws/deposits")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "finance:deposit:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].depositNo").value("DEP_TEST_FROZEN"));
    }

    @Test
    void getDepositRecord_success() throws Exception {
        final DepositRecordDTO depositDTO = new DepositRecordDTO();
        depositDTO.setId(790501L);
        depositDTO.setDepositNo("DEP_TEST_FROZEN");
        when(withdrawService.getDepositRecord(790501L)).thenReturn(depositDTO);

        mockMvc.perform(get("/api/finance/withdraws/deposits/790501")
                        .with(user("tester").authorities(() -> "finance:deposit:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.depositNo").value("DEP_TEST_FROZEN"));
    }

    @Test
    void pageWithdrawApplies_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/withdraws")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:withdraw:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }

    @Test
    void pageDepositRecords_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/withdraws/deposits")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:deposit:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
