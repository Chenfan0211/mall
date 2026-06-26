package com.mall.aftersale.controller;

import com.mall.aftersale.service.AfterSaleService;
import com.mall.api.aftersale.dto.AfterSaleAuditLogDTO;
import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.vo.AfterSaleQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
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
@RequestMapping("/api/aftersale/after-sales")
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    public AfterSaleController(final AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('aftersale:after-sale:view')")
    public Result<PageResult<AfterSaleDTO>> pageAfterSales(@Valid final AfterSaleQueryVO query) {
        return Result.success(afterSaleService.pageAfterSales(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('aftersale:after-sale:view')")
    public Result<AfterSaleDTO> getAfterSale(@PathVariable final Long id) {
        return Result.success(afterSaleService.getAfterSale(id));
    }

    @GetMapping("/{id}/audit-logs")
    @PreAuthorize("hasAuthority('aftersale:audit-log:view')")
    public Result<List<AfterSaleAuditLogDTO>> listAuditLogs(@PathVariable final Long id) {
        return Result.success(afterSaleService.listAuditLogs(id));
    }
}
