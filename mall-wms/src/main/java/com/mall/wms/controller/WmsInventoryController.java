package com.mall.wms.controller;

import com.mall.api.wms.dto.InventoryDTO;
import com.mall.api.wms.dto.InventoryLockDTO;
import com.mall.api.wms.dto.InventoryLogDTO;
import com.mall.api.wms.dto.StocktakeOrderDTO;
import com.mall.api.wms.vo.WmsInventoryQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.wms.service.WmsInventoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/wms/inventory")
public class WmsInventoryController {

    private final WmsInventoryService wmsInventoryService;

    public WmsInventoryController(final WmsInventoryService wmsInventoryService) {
        this.wmsInventoryService = wmsInventoryService;
    }

    @GetMapping("/stocks")
    @PreAuthorize("hasAuthority('wms:inventory:view')")
    public Result<PageResult<InventoryDTO>> pageInventories(@Valid final WmsInventoryQueryVO query) {
        return Result.success(wmsInventoryService.pageInventories(query));
    }

    @GetMapping("/stocks/{id}")
    @PreAuthorize("hasAuthority('wms:inventory:view')")
    public Result<InventoryDTO> getInventory(@PathVariable final Long id) {
        return Result.success(wmsInventoryService.getInventory(id));
    }

    @GetMapping("/locks")
    @PreAuthorize("hasAuthority('wms:inventory-lock:view')")
    public Result<PageResult<InventoryLockDTO>> pageInventoryLocks(@Valid final WmsInventoryQueryVO query) {
        return Result.success(wmsInventoryService.pageInventoryLocks(query));
    }

    @GetMapping("/locks/{id}")
    @PreAuthorize("hasAuthority('wms:inventory-lock:view')")
    public Result<InventoryLockDTO> getInventoryLock(@PathVariable final Long id) {
        return Result.success(wmsInventoryService.getInventoryLock(id));
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('wms:inventory-log:view')")
    public Result<PageResult<InventoryLogDTO>> pageInventoryLogs(@Valid final WmsInventoryQueryVO query) {
        return Result.success(wmsInventoryService.pageInventoryLogs(query));
    }

    @GetMapping("/logs/{id}")
    @PreAuthorize("hasAuthority('wms:inventory-log:view')")
    public Result<InventoryLogDTO> getInventoryLog(@PathVariable final Long id) {
        return Result.success(wmsInventoryService.getInventoryLog(id));
    }

    @GetMapping("/stocktakes")
    @PreAuthorize("hasAuthority('wms:stocktake:view')")
    public Result<PageResult<StocktakeOrderDTO>> pageStocktakeOrders(@Valid final WmsInventoryQueryVO query) {
        return Result.success(wmsInventoryService.pageStocktakeOrders(query));
    }

    @GetMapping("/stocktakes/{id}")
    @PreAuthorize("hasAuthority('wms:stocktake:view')")
    public Result<StocktakeOrderDTO> getStocktakeOrder(@PathVariable final Long id) {
        return Result.success(wmsInventoryService.getStocktakeOrder(id));
    }
}
