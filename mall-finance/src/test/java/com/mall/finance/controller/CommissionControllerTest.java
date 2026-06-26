package com.mall.finance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.SplitFlowDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.finance.service.CommissionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommissionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CommissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommissionService commissionService;

    @Test
    void pageCommissionDetails_success() throws Exception {
        final CommissionDetailDTO commissionDTO = new CommissionDetailDTO();
        commissionDTO.setId(790201L);
        commissionDTO.setCommissionNo("COM_TEST_WAIT_CALC");
        when(commissionService.pageCommissionDetails(any())).thenReturn(PageResult.of(1L, List.of(commissionDTO)));

        mockMvc.perform(get("/api/finance/commissions")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "finance:commission:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].commissionNo").value("COM_TEST_WAIT_CALC"));
    }

    @Test
    void getCommissionDetail_success() throws Exception {
        final CommissionDetailDTO commissionDTO = new CommissionDetailDTO();
        commissionDTO.setId(790201L);
        commissionDTO.setCommissionNo("COM_TEST_WAIT_CALC");
        when(commissionService.getCommissionDetail(790201L)).thenReturn(commissionDTO);

        mockMvc.perform(get("/api/finance/commissions/790201")
                        .with(user("tester").authorities(() -> "finance:commission:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.commissionNo").value("COM_TEST_WAIT_CALC"));
    }

    @Test
    void pageSplitFlows_success() throws Exception {
        final SplitFlowDTO splitDTO = new SplitFlowDTO();
        splitDTO.setId(790301L);
        splitDTO.setSplitNo("SPLIT_TEST_WAIT");
        when(commissionService.pageSplitFlows(any())).thenReturn(PageResult.of(1L, List.of(splitDTO)));

        mockMvc.perform(get("/api/finance/commissions/splits")
                        .param("splitStatus", "10")
                        .with(user("tester").authorities(() -> "finance:split:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].splitNo").value("SPLIT_TEST_WAIT"));
    }

    @Test
    void getSplitFlow_success() throws Exception {
        final SplitFlowDTO splitDTO = new SplitFlowDTO();
        splitDTO.setId(790301L);
        splitDTO.setSplitNo("SPLIT_TEST_WAIT");
        when(commissionService.getSplitFlow(790301L)).thenReturn(splitDTO);

        mockMvc.perform(get("/api/finance/commissions/splits/790301")
                        .with(user("tester").authorities(() -> "finance:split:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.splitNo").value("SPLIT_TEST_WAIT"));
    }

    @Test
    void pageCommissionDetails_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/commissions")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:commission:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }

    @Test
    void pageSplitFlows_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/commissions/splits")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:split:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
