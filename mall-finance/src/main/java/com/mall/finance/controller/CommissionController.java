package com.mall.finance.controller;

import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.SplitFlowDTO;
import com.mall.api.finance.vo.CommissionDetailQueryVO;
import com.mall.api.finance.vo.SplitFlowQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.finance.service.CommissionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/finance/commissions")
public class CommissionController {

    private final CommissionService commissionService;

    public CommissionController(final CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('finance:commission:view')")
    public Result<PageResult<CommissionDetailDTO>> pageCommissionDetails(
            @Valid final CommissionDetailQueryVO query) {
        return Result.success(commissionService.pageCommissionDetails(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('finance:commission:view')")
    public Result<CommissionDetailDTO> getCommissionDetail(@PathVariable final Long id) {
        return Result.success(commissionService.getCommissionDetail(id));
    }

    @GetMapping("/splits")
    @PreAuthorize("hasAuthority('finance:split:view')")
    public Result<PageResult<SplitFlowDTO>> pageSplitFlows(@Valid final SplitFlowQueryVO query) {
        return Result.success(commissionService.pageSplitFlows(query));
    }

    @GetMapping("/splits/{id}")
    @PreAuthorize("hasAuthority('finance:split:view')")
    public Result<SplitFlowDTO> getSplitFlow(@PathVariable final Long id) {
        return Result.success(commissionService.getSplitFlow(id));
    }
}
