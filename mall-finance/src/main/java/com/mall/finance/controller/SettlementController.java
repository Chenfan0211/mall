package com.mall.finance.controller;

import com.mall.api.finance.dto.ReconciliationBatchDTO;
import com.mall.api.finance.dto.SupplierSettlementDTO;
import com.mall.api.finance.vo.ReconciliationBatchQueryVO;
import com.mall.api.finance.vo.SupplierSettlementQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.finance.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/finance/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(final SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/supplier")
    @PreAuthorize("hasAuthority('finance:supplier-settlement:view')")
    public Result<PageResult<SupplierSettlementDTO>> pageSupplierSettlements(
            @Valid final SupplierSettlementQueryVO query) {
        return Result.success(settlementService.pageSupplierSettlements(query));
    }

    @GetMapping("/supplier/{id}")
    @PreAuthorize("hasAuthority('finance:supplier-settlement:view')")
    public Result<SupplierSettlementDTO> getSupplierSettlement(@PathVariable final Long id) {
        return Result.success(settlementService.getSupplierSettlement(id));
    }

    @GetMapping("/reconciliations")
    @PreAuthorize("hasAuthority('finance:reconciliation:view')")
    public Result<PageResult<ReconciliationBatchDTO>> pageReconciliationBatches(
            @Valid final ReconciliationBatchQueryVO query) {
        return Result.success(settlementService.pageReconciliationBatches(query));
    }

    @GetMapping("/reconciliations/{id}")
    @PreAuthorize("hasAuthority('finance:reconciliation:view')")
    public Result<ReconciliationBatchDTO> getReconciliationBatch(@PathVariable final Long id) {
        return Result.success(settlementService.getReconciliationBatch(id));
    }
}
