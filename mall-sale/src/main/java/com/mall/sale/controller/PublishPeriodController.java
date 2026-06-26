package com.mall.sale.controller;

import com.mall.api.sale.dto.PeriodSnapshotDTO;
import com.mall.api.sale.dto.PublishPeriodDTO;
import com.mall.api.sale.vo.PublishPeriodQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.sale.service.PublishPeriodService;
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
@RequestMapping("/api/sale/periods")
public class PublishPeriodController {

    private final PublishPeriodService publishPeriodService;

    public PublishPeriodController(final PublishPeriodService publishPeriodService) {
        this.publishPeriodService = publishPeriodService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('sale:period:view')")
    public Result<PageResult<PublishPeriodDTO>> pagePeriods(@Valid final PublishPeriodQueryVO query) {
        return Result.success(publishPeriodService.pagePeriods(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sale:period:view')")
    public Result<PublishPeriodDTO> getPeriod(@PathVariable final Long id) {
        return Result.success(publishPeriodService.getPeriod(id));
    }

    @GetMapping("/{id}/snapshots")
    @PreAuthorize("hasAuthority('sale:period:view')")
    public Result<List<PeriodSnapshotDTO>> listSnapshots(@PathVariable final Long id) {
        return Result.success(publishPeriodService.listSnapshots(id));
    }
}
