package com.mall.supplier.controller;

import com.mall.api.supplier.dto.PurchaseAuditLogDTO;
import com.mall.api.supplier.dto.PurchaseItemDTO;
import com.mall.api.supplier.dto.PurchaseOrderDTO;
import com.mall.api.supplier.dto.PurchaseSourceLogDTO;
import com.mall.api.supplier.vo.PurchaseItemQueryVO;
import com.mall.api.supplier.vo.PurchaseOrderQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.supplier.service.PurchaseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/supplier/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(final PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    @PreAuthorize("@portalAuthorization.isPortal('role-workbench-h5') or hasAuthority('purchase:order:view')")
    public Result<PageResult<PurchaseOrderDTO>> pagePurchaseOrders(@Valid final PurchaseOrderQueryVO query) {
        return Result.success(purchaseService.pagePurchaseOrders(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@portalAuthorization.isPortal('role-workbench-h5') or hasAuthority('purchase:order:view')")
    public Result<PurchaseOrderDTO> getPurchaseOrder(@PathVariable final Long id) {
        return Result.success(purchaseService.getPurchaseOrder(id));
    }

    @GetMapping("/items")
    @PreAuthorize("@portalAuthorization.isPortal('role-workbench-h5') or hasAuthority('purchase:item:view')")
    public Result<PageResult<PurchaseItemDTO>> pagePurchaseItems(@Valid final PurchaseItemQueryVO query) {
        return Result.success(purchaseService.pagePurchaseItems(query));
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("@portalAuthorization.isPortal('role-workbench-h5') or hasAuthority('purchase:item:view')")
    public Result<PurchaseItemDTO> getPurchaseItem(@PathVariable final Long id) {
        return Result.success(purchaseService.getPurchaseItem(id));
    }

    @GetMapping("/{id}/audit-logs")
    @PreAuthorize("@portalAuthorization.isPortal('role-workbench-h5') or hasAuthority('purchase:audit-log:view')")
    public Result<List<PurchaseAuditLogDTO>> listAuditLogs(@PathVariable final Long id) {
        return Result.success(purchaseService.listAuditLogs(id));
    }

    @GetMapping("/{id}/source-logs")
    @PreAuthorize("@portalAuthorization.isPortal('role-workbench-h5') or hasAuthority('purchase:source-log:view')")
    public Result<List<PurchaseSourceLogDTO>> listSourceLogs(@PathVariable final Long id) {
        return Result.success(purchaseService.listSourceLogs(id));
    }
}
