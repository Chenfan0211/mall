package com.mall.supplier.controller;

import com.mall.api.supplier.dto.SupplierWorkbenchDTO;
import com.mall.api.supplier.vo.SupplierWorkbenchQueryVO;
import com.mall.common.result.Result;
import com.mall.supplier.service.SupplierWorkbenchService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/supplier/workbench")
public class SupplierWorkbenchController {

    private final SupplierWorkbenchService supplierWorkbenchService;

    public SupplierWorkbenchController(final SupplierWorkbenchService supplierWorkbenchService) {
        this.supplierWorkbenchService = supplierWorkbenchService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('supplier:workbench:view')")
    public Result<SupplierWorkbenchDTO> getWorkbench(@Valid final SupplierWorkbenchQueryVO query) {
        return Result.success(supplierWorkbenchService.getWorkbench(query));
    }
}
