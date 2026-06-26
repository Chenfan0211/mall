package com.mall.finance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.finance.dto.ReconciliationBatchDTO;
import com.mall.api.finance.dto.SupplierSettlementDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.finance.service.SettlementService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SettlementController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SettlementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SettlementService settlementService;

    @Test
    void pageSupplierSettlements_success() throws Exception {
        final SupplierSettlementDTO settlementDTO = new SupplierSettlementDTO();
        settlementDTO.setId(790601L);
        settlementDTO.setSettlementNo("SETTLE_TEST_WAIT");
        when(settlementService.pageSupplierSettlements(any())).thenReturn(PageResult.of(1L, List.of(settlementDTO)));

        mockMvc.perform(get("/api/finance/settlements/supplier")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "finance:supplier-settlement:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].settlementNo").value("SETTLE_TEST_WAIT"));
    }

    @Test
    void getSupplierSettlement_success() throws Exception {
        final SupplierSettlementDTO settlementDTO = new SupplierSettlementDTO();
        settlementDTO.setId(790601L);
        settlementDTO.setSettlementNo("SETTLE_TEST_WAIT");
        when(settlementService.getSupplierSettlement(790601L)).thenReturn(settlementDTO);

        mockMvc.perform(get("/api/finance/settlements/supplier/790601")
                        .with(user("tester").authorities(() -> "finance:supplier-settlement:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.settlementNo").value("SETTLE_TEST_WAIT"));
    }

    @Test
    void pageReconciliationBatches_success() throws Exception {
        final ReconciliationBatchDTO batchDTO = new ReconciliationBatchDTO();
        batchDTO.setId(790701L);
        batchDTO.setReconcileNo("RECON_TEST_WAIT");
        when(settlementService.pageReconciliationBatches(any())).thenReturn(PageResult.of(1L, List.of(batchDTO)));

        mockMvc.perform(get("/api/finance/settlements/reconciliations")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "finance:reconciliation:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].reconcileNo").value("RECON_TEST_WAIT"));
    }

    @Test
    void getReconciliationBatch_success() throws Exception {
        final ReconciliationBatchDTO batchDTO = new ReconciliationBatchDTO();
        batchDTO.setId(790701L);
        batchDTO.setReconcileNo("RECON_TEST_WAIT");
        when(settlementService.getReconciliationBatch(790701L)).thenReturn(batchDTO);

        mockMvc.perform(get("/api/finance/settlements/reconciliations/790701")
                        .with(user("tester").authorities(() -> "finance:reconciliation:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.reconcileNo").value("RECON_TEST_WAIT"));
    }

    @Test
    void pageSupplierSettlements_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/settlements/supplier")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:supplier-settlement:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }

    @Test
    void pageReconciliationBatches_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/finance/settlements/reconciliations")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "finance:reconciliation:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
