package com.mall.operation.controller;

import com.mall.api.operation.dto.OperationDashboardSummaryDTO;
import com.mall.common.result.Result;
import com.mall.operation.service.OperationDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operation/dashboard")
public class OperationDashboardController {

    private final OperationDashboardService operationDashboardService;

    public OperationDashboardController(final OperationDashboardService operationDashboardService) {
        this.operationDashboardService = operationDashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('operation:dashboard:view')")
    public Result<OperationDashboardSummaryDTO> getSummary() {
        return Result.success(operationDashboardService.getSummary());
    }
}
