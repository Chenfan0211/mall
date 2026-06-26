package com.mall.system.controller;

import com.mall.api.system.dto.MenuDTO;
import com.mall.common.result.Result;
import com.mall.system.service.MenuService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/system/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(final MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:view')")
    public Result<List<MenuDTO>> listVisibleMenus(@RequestParam(required = false) final String portalCode) {
        return Result.success(menuService.listVisibleMenus(portalCode));
    }
}
