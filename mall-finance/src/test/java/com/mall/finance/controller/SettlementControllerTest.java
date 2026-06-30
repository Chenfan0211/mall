package com.mall.finance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.finance.dto.ReconciliationBatchDTO;
import com.mall.api.finance.dto.SupplierSettlementDTO;
import com.mall.common.page.PageResult;
import com.mall.common.security.MallLoginUser;
import com.mall.common.security.PortalAuthorization;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.finance.service.SettlementService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SettlementController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, SettlementControllerTest.TestConfig.class})
class SettlementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SettlementService settlementService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @EnableMethodSecurity
    static class TestConfig {

        @Bean
        PortalAuthorization portalAuthorization() {
            return new PortalAuthorization();
        }
    }

    @Test
    void pageSupplierSettlements_success() throws Exception {
        final SupplierSettlementDTO settlementDTO = new SupplierSettlementDTO();
        settlementDTO.setId(790601L);
        settlementDTO.setSettlementNo("SETTLE_TEST_WAIT");
        when(settlementService.pageSupplierSettlements(any())).thenReturn(PageResult.of(1L, List.of(settlementDTO)));
        useBackendAuthentication("finance:supplier-settlement:view");

        mockMvc.perform(get("/api/finance/settlements/supplier")
                        .param("status", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].settlementNo").value("SETTLE_TEST_WAIT"));
    }

    @Test
    void pageSupplierSettlements_roleWorkbenchPortal_success() throws Exception {
        final SupplierSettlementDTO settlementDTO = new SupplierSettlementDTO();
        settlementDTO.setId(790601L);
        settlementDTO.setSettlementNo("SETTLE_TEST_WAIT");
        when(settlementService.pageSupplierSettlements(any())).thenReturn(PageResult.of(1L, List.of(settlementDTO)));
        useAuthentication(roleWorkbenchAuthentication());

        mockMvc.perform(get("/api/finance/settlements/supplier")
                        .param("supplierId", "710001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].settlementNo").value("SETTLE_TEST_WAIT"));
    }

    @Test
    void pageSupplierSettlements_withoutPermissionAndPortal_forbidden() throws Exception {
        useBackendAuthentication("unused");

        mockMvc.perform(get("/api/finance/settlements/supplier")
                        .param("supplierId", "710001"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void getSupplierSettlement_success() throws Exception {
        final SupplierSettlementDTO settlementDTO = new SupplierSettlementDTO();
        settlementDTO.setId(790601L);
        settlementDTO.setSettlementNo("SETTLE_TEST_WAIT");
        when(settlementService.getSupplierSettlement(790601L)).thenReturn(settlementDTO);
        useBackendAuthentication("finance:supplier-settlement:view");

        mockMvc.perform(get("/api/finance/settlements/supplier/790601"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.settlementNo").value("SETTLE_TEST_WAIT"));
    }

    @Test
    void getSupplierSettlement_roleWorkbenchPortal_success() throws Exception {
        final SupplierSettlementDTO settlementDTO = new SupplierSettlementDTO();
        settlementDTO.setId(790601L);
        settlementDTO.setSettlementNo("SETTLE_TEST_WAIT");
        when(settlementService.getSupplierSettlement(790601L)).thenReturn(settlementDTO);
        useAuthentication(roleWorkbenchAuthentication());

        mockMvc.perform(get("/api/finance/settlements/supplier/790601"))
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
        useBackendAuthentication("finance:reconciliation:view");

        mockMvc.perform(get("/api/finance/settlements/reconciliations")
                        .param("status", "10"))
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
        useBackendAuthentication("finance:reconciliation:view");

        mockMvc.perform(get("/api/finance/settlements/reconciliations/790701"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.reconcileNo").value("RECON_TEST_WAIT"));
    }

    @Test
    void pageSupplierSettlements_invalidPageSize_returnParamError() throws Exception {
        useBackendAuthentication("finance:supplier-settlement:view");

        mockMvc.perform(get("/api/finance/settlements/supplier")
                        .param("pageSize", "201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }

    @Test
    void pageReconciliationBatches_invalidPageSize_returnParamError() throws Exception {
        useBackendAuthentication("finance:reconciliation:view");

        mockMvc.perform(get("/api/finance/settlements/reconciliations")
                        .param("pageSize", "201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }

    private UsernamePasswordAuthenticationToken roleWorkbenchAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                new MallLoginUser(700014L, "test_supplier_h5", "role-workbench-h5"),
                null,
                List.of(new SimpleGrantedAuthority("unused")));
    }

    private void useBackendAuthentication(final String authority) {
        useAuthentication(new UsernamePasswordAuthenticationToken(
                new MallLoginUser(700013L, "tester", "admin"),
                null,
                List.of(new SimpleGrantedAuthority(authority))));
    }

    private void useAuthentication(final UsernamePasswordAuthenticationToken authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
