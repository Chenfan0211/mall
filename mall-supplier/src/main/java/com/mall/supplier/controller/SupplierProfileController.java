package com.mall.supplier.controller;

import com.mall.api.supplier.dto.SupplierContactDTO;
import com.mall.api.supplier.dto.SupplierContractDTO;
import com.mall.api.supplier.dto.SupplierDTO;
import com.mall.api.supplier.dto.SupplierQualificationDTO;
import com.mall.api.supplier.vo.SupplierContactQueryVO;
import com.mall.api.supplier.vo.SupplierContractQueryVO;
import com.mall.api.supplier.vo.SupplierQualificationQueryVO;
import com.mall.api.supplier.vo.SupplierQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.supplier.service.SupplierProfileService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/supplier/profiles")
public class SupplierProfileController {

    private final SupplierProfileService supplierProfileService;

    public SupplierProfileController(final SupplierProfileService supplierProfileService) {
        this.supplierProfileService = supplierProfileService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('supplier:profile:view')")
    public Result<PageResult<SupplierDTO>> pageSuppliers(@Valid final SupplierQueryVO query) {
        return Result.success(supplierProfileService.pageSuppliers(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:profile:view')")
    public Result<SupplierDTO> getSupplier(@PathVariable final Long id) {
        return Result.success(supplierProfileService.getSupplier(id));
    }

    @GetMapping("/contacts")
    @PreAuthorize("hasAuthority('supplier:contact:view')")
    public Result<PageResult<SupplierContactDTO>> pageContacts(@Valid final SupplierContactQueryVO query) {
        return Result.success(supplierProfileService.pageContacts(query));
    }

    @GetMapping("/contacts/{id}")
    @PreAuthorize("hasAuthority('supplier:contact:view')")
    public Result<SupplierContactDTO> getContact(@PathVariable final Long id) {
        return Result.success(supplierProfileService.getContact(id));
    }

    @GetMapping("/qualifications")
    @PreAuthorize("hasAuthority('supplier:qualification:view')")
    public Result<PageResult<SupplierQualificationDTO>> pageQualifications(
            @Valid final SupplierQualificationQueryVO query) {
        return Result.success(supplierProfileService.pageQualifications(query));
    }

    @GetMapping("/qualifications/{id}")
    @PreAuthorize("hasAuthority('supplier:qualification:view')")
    public Result<SupplierQualificationDTO> getQualification(@PathVariable final Long id) {
        return Result.success(supplierProfileService.getQualification(id));
    }

    @GetMapping("/contracts")
    @PreAuthorize("hasAuthority('supplier:contract:view')")
    public Result<PageResult<SupplierContractDTO>> pageContracts(@Valid final SupplierContractQueryVO query) {
        return Result.success(supplierProfileService.pageContracts(query));
    }

    @GetMapping("/contracts/{id}")
    @PreAuthorize("hasAuthority('supplier:contract:view')")
    public Result<SupplierContractDTO> getContract(@PathVariable final Long id) {
        return Result.success(supplierProfileService.getContract(id));
    }
}
