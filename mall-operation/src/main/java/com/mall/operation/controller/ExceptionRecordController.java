package com.mall.operation.controller;

import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.operation.vo.ExceptionRecordQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.operation.service.ExceptionRecordService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/operation/exceptions")
public class ExceptionRecordController {

    private final ExceptionRecordService exceptionRecordService;

    public ExceptionRecordController(final ExceptionRecordService exceptionRecordService) {
        this.exceptionRecordService = exceptionRecordService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('operation:exception:view')")
    public Result<PageResult<ExceptionRecordDTO>> pageExceptionRecords(@Valid final ExceptionRecordQueryVO query) {
        return Result.success(exceptionRecordService.pageExceptionRecords(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('operation:exception:view')")
    public Result<ExceptionRecordDTO> getExceptionRecord(@PathVariable final Long id) {
        return Result.success(exceptionRecordService.getExceptionRecord(id));
    }
}
