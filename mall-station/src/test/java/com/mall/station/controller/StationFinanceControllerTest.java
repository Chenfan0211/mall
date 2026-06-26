package com.mall.station.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.station.dto.StationMessageDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.station.service.StationFinanceService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StationFinanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StationFinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationFinanceService stationFinanceService;

    @Test
    void pageAccounts_success() throws Exception {
        final FinanceAccountDTO accountDTO = new FinanceAccountDTO();
        accountDTO.setId(790002L);
        accountDTO.setAccountNo("FIN_ACC_STATION");
        when(stationFinanceService.pageAccounts(any())).thenReturn(PageResult.of(1L, List.of(accountDTO)));

        mockMvc.perform(get("/api/station/finance/accounts")
                        .param("subjectType", "2")
                        .with(user("tester").authorities(() -> "station:finance:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].accountNo").value("FIN_ACC_STATION"));
    }

    @Test
    void pageMessages_success() throws Exception {
        final StationMessageDTO messageDTO = new StationMessageDTO();
        messageDTO.setId(792102L);
        messageDTO.setMsgNo("MSG_TEST_SUCCESS_UNREAD");
        when(stationFinanceService.pageMessages(any())).thenReturn(PageResult.of(1L, List.of(messageDTO)));

        mockMvc.perform(get("/api/station/finance/messages")
                        .param("receiverType", "4")
                        .with(user("tester").authorities(() -> "station:message:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].msgNo").value("MSG_TEST_SUCCESS_UNREAD"));
    }

    @Test
    void pageWithdraws_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/station/finance/withdraws")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "station:withdraw:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void applyWithdraw_success() throws Exception {
        final WithdrawApplyDTO withdrawDTO = new WithdrawApplyDTO();
        withdrawDTO.setId(790407L);
        withdrawDTO.setAuditStatus(10L);
        when(stationFinanceService.applyWithdraw(any())).thenReturn(withdrawDTO);

        mockMvc.perform(post("/api/station/finance/withdraws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":790002,\"withdrawAmount\":100.00,"
                                + "\"receiveAccountName\":\"自提点负责人A\",\"receiveAccountNo\":\"622200000001\"}")
                        .with(user("tester").authorities(() -> "station:withdraw:apply")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.auditStatus").value("10"));
    }

    @Test
    void cancelWithdraw_success() throws Exception {
        final WithdrawApplyDTO withdrawDTO = new WithdrawApplyDTO();
        withdrawDTO.setId(790401L);
        withdrawDTO.setAuditStatus(40L);
        when(stationFinanceService.cancelWithdraw(eq(790401L))).thenReturn(withdrawDTO);

        mockMvc.perform(post("/api/station/finance/withdraws/790401/cancel")
                        .with(user("tester").authorities(() -> "station:withdraw:cancel")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.auditStatus").value("40"));
    }

    @Test
    void markMessageRead_success() throws Exception {
        final StationMessageDTO messageDTO = new StationMessageDTO();
        messageDTO.setId(792102L);
        messageDTO.setReadStatus(1L);
        when(stationFinanceService.markMessageRead(eq(792102L))).thenReturn(messageDTO);

        mockMvc.perform(post("/api/station/finance/messages/792102/read")
                        .with(user("tester").authorities(() -> "station:message:read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.readStatus").value("1"));
    }

    @Test
    void applyWithdraw_missingAccount_returnParamError() throws Exception {
        mockMvc.perform(post("/api/station/finance/withdraws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"withdrawAmount\":100.00,\"receiveAccountName\":\"自提点负责人A\","
                                + "\"receiveAccountNo\":\"622200000001\"}")
                        .with(user("tester").authorities(() -> "station:withdraw:apply")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
