package com.mall.aftersale.controller;

import com.mall.aftersale.service.AfterSaleItemService;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.aftersale.vo.AfterSaleItemQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/aftersale/items")
public class AfterSaleItemController {

    private final AfterSaleItemService afterSaleItemService;

    public AfterSaleItemController(final AfterSaleItemService afterSaleItemService) {
        this.afterSaleItemService = afterSaleItemService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('aftersale:item:view')")
    public Result<PageResult<AfterSaleItemDTO>> pageItems(@Valid final AfterSaleItemQueryVO query) {
        return Result.success(afterSaleItemService.pageItems(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('aftersale:item:view')")
    public Result<AfterSaleItemDTO> getItem(@PathVariable final Long id) {
        return Result.success(afterSaleItemService.getItem(id));
    }
}
