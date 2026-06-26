package com.mall.system.controller;

import com.mall.api.system.dto.AccountDTO;
import com.mall.api.system.vo.AccountQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.system.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/system/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:account:view')")
    public Result<PageResult<AccountDTO>> pageAccounts(@Valid final AccountQueryVO query) {
        return Result.success(accountService.pageAccounts(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:account:view')")
    public Result<AccountDTO> getAccount(@PathVariable final Long id) {
        return Result.success(accountService.getAccount(id));
    }
}
