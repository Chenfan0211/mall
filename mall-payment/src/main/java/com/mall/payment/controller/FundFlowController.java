package com.mall.payment.controller;

import com.mall.api.payment.dto.FundFlowDTO;
import com.mall.api.payment.vo.FundFlowQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.payment.service.FundFlowService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/payment/fund-flows")
public class FundFlowController {

    private final FundFlowService fundFlowService;

    public FundFlowController(final FundFlowService fundFlowService) {
        this.fundFlowService = fundFlowService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('payment:fund-flow:view')")
    public Result<PageResult<FundFlowDTO>> pageFlows(@Valid final FundFlowQueryVO query) {
        return Result.success(fundFlowService.pageFlows(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:fund-flow:view')")
    public Result<FundFlowDTO> getFlow(@PathVariable final Long id) {
        return Result.success(fundFlowService.getFlow(id));
    }
}
