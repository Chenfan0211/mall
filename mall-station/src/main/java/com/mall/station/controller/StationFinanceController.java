package com.mall.station.controller;

import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.station.dto.StationMessageDTO;
import com.mall.api.station.vo.StationFinanceQueryVO;
import com.mall.api.station.vo.StationMessageQueryVO;
import com.mall.api.station.vo.StationWithdrawApplyVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.station.service.StationFinanceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/station/finance")
public class StationFinanceController {

    private final StationFinanceService stationFinanceService;

    public StationFinanceController(final StationFinanceService stationFinanceService) {
        this.stationFinanceService = stationFinanceService;
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasAuthority('station:finance:view')")
    public Result<PageResult<FinanceAccountDTO>> pageAccounts(@Valid final StationFinanceQueryVO query) {
        return Result.success(stationFinanceService.pageAccounts(query));
    }

    @GetMapping("/accounts/{id}")
    @PreAuthorize("hasAuthority('station:finance:view')")
    public Result<FinanceAccountDTO> getAccount(@PathVariable final Long id) {
        return Result.success(stationFinanceService.getAccount(id));
    }

    @GetMapping("/account-flows")
    @PreAuthorize("hasAuthority('station:finance:view')")
    public Result<PageResult<FinanceAccountFlowDTO>> pageAccountFlows(@Valid final StationFinanceQueryVO query) {
        return Result.success(stationFinanceService.pageAccountFlows(query));
    }

    @GetMapping("/account-flows/{id}")
    @PreAuthorize("hasAuthority('station:finance:view')")
    public Result<FinanceAccountFlowDTO> getAccountFlow(@PathVariable final Long id) {
        return Result.success(stationFinanceService.getAccountFlow(id));
    }

    @GetMapping("/commissions")
    @PreAuthorize("hasAuthority('station:finance:view')")
    public Result<PageResult<CommissionDetailDTO>> pageCommissions(@Valid final StationFinanceQueryVO query) {
        return Result.success(stationFinanceService.pageCommissions(query));
    }

    @GetMapping("/commissions/{id}")
    @PreAuthorize("hasAuthority('station:finance:view')")
    public Result<CommissionDetailDTO> getCommission(@PathVariable final Long id) {
        return Result.success(stationFinanceService.getCommission(id));
    }

    @GetMapping("/withdraws")
    @PreAuthorize("hasAuthority('station:withdraw:view')")
    public Result<PageResult<WithdrawApplyDTO>> pageWithdraws(@Valid final StationFinanceQueryVO query) {
        return Result.success(stationFinanceService.pageWithdraws(query));
    }

    @GetMapping("/withdraws/{id}")
    @PreAuthorize("hasAuthority('station:withdraw:view')")
    public Result<WithdrawApplyDTO> getWithdraw(@PathVariable final Long id) {
        return Result.success(stationFinanceService.getWithdraw(id));
    }

    @PostMapping("/withdraws")
    @PreAuthorize("hasAuthority('station:withdraw:apply')")
    public Result<WithdrawApplyDTO> applyWithdraw(@Valid @RequestBody final StationWithdrawApplyVO request) {
        return Result.success(stationFinanceService.applyWithdraw(request));
    }

    @PostMapping("/withdraws/{id}/cancel")
    @PreAuthorize("hasAuthority('station:withdraw:cancel')")
    public Result<WithdrawApplyDTO> cancelWithdraw(@PathVariable final Long id) {
        return Result.success(stationFinanceService.cancelWithdraw(id));
    }

    @GetMapping("/messages")
    @PreAuthorize("hasAuthority('station:message:view')")
    public Result<PageResult<StationMessageDTO>> pageMessages(@Valid final StationMessageQueryVO query) {
        return Result.success(stationFinanceService.pageMessages(query));
    }

    @GetMapping("/messages/{id}")
    @PreAuthorize("hasAuthority('station:message:view')")
    public Result<StationMessageDTO> getMessage(@PathVariable final Long id) {
        return Result.success(stationFinanceService.getMessage(id));
    }

    @PostMapping("/messages/{id}/read")
    @PreAuthorize("hasAuthority('station:message:read')")
    public Result<StationMessageDTO> markMessageRead(@PathVariable final Long id) {
        return Result.success(stationFinanceService.markMessageRead(id));
    }
}
