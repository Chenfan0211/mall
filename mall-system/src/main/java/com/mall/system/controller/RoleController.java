package com.mall.system.controller;

import com.mall.api.system.dto.RoleDTO;
import com.mall.api.system.vo.RoleQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.system.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/system/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(final RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<PageResult<RoleDTO>> pageRoles(@Valid final RoleQueryVO query) {
        return Result.success(roleService.pageRoles(query));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<List<RoleDTO>> listAccountRoles(@PathVariable final Long accountId) {
        return Result.success(roleService.listByAccountId(accountId));
    }
}
