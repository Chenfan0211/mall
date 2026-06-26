package com.mall.finance.controller;

import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.vo.FinanceAccountFlowQueryVO;
import com.mall.api.finance.vo.FinanceAccountQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.finance.service.FinanceAccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/finance/accounts")
public class FinanceAccountController {

    private final FinanceAccountService financeAccountService;

    public FinanceAccountController(final FinanceAccountService financeAccountService) {
        this.financeAccountService = financeAccountService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('finance:account:view')")
    public Result<PageResult<FinanceAccountDTO>> pageAccounts(@Valid final FinanceAccountQueryVO query) {
        return Result.success(financeAccountService.pageAccounts(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('finance:account:view')")
    public Result<FinanceAccountDTO> getAccount(@PathVariable final Long id) {
        return Result.success(financeAccountService.getAccount(id));
    }

    @GetMapping("/flows")
    @PreAuthorize("hasAuthority('finance:account-flow:view')")
    public Result<PageResult<FinanceAccountFlowDTO>> pageAccountFlows(
            @Valid final FinanceAccountFlowQueryVO query) {
        return Result.success(financeAccountService.pageAccountFlows(query));
    }

    @GetMapping("/flows/{id}")
    @PreAuthorize("hasAuthority('finance:account-flow:view')")
    public Result<FinanceAccountFlowDTO> getAccountFlow(@PathVariable final Long id) {
        return Result.success(financeAccountService.getAccountFlow(id));
    }
}
