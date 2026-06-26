package com.mall.finance.controller;

import com.mall.api.finance.dto.DepositRecordDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.finance.vo.DepositRecordQueryVO;
import com.mall.api.finance.vo.WithdrawApplyQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.finance.service.WithdrawService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/finance/withdraws")
public class WithdrawController {

    private final WithdrawService withdrawService;

    public WithdrawController(final WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('finance:withdraw:view')")
    public Result<PageResult<WithdrawApplyDTO>> pageWithdrawApplies(@Valid final WithdrawApplyQueryVO query) {
        return Result.success(withdrawService.pageWithdrawApplies(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('finance:withdraw:view')")
    public Result<WithdrawApplyDTO> getWithdrawApply(@PathVariable final Long id) {
        return Result.success(withdrawService.getWithdrawApply(id));
    }

    @GetMapping("/deposits")
    @PreAuthorize("hasAuthority('finance:deposit:view')")
    public Result<PageResult<DepositRecordDTO>> pageDepositRecords(@Valid final DepositRecordQueryVO query) {
        return Result.success(withdrawService.pageDepositRecords(query));
    }

    @GetMapping("/deposits/{id}")
    @PreAuthorize("hasAuthority('finance:deposit:view')")
    public Result<DepositRecordDTO> getDepositRecord(@PathVariable final Long id) {
        return Result.success(withdrawService.getDepositRecord(id));
    }
}
