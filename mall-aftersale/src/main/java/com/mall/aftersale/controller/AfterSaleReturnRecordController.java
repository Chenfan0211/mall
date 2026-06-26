package com.mall.aftersale.controller;

import com.mall.aftersale.service.AfterSaleReturnRecordService;
import com.mall.api.aftersale.dto.AfterSaleReturnRecordDTO;
import com.mall.api.aftersale.vo.AfterSaleReturnRecordQueryVO;
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
@RequestMapping("/api/aftersale/return-records")
public class AfterSaleReturnRecordController {

    private final AfterSaleReturnRecordService returnRecordService;

    public AfterSaleReturnRecordController(final AfterSaleReturnRecordService returnRecordService) {
        this.returnRecordService = returnRecordService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('aftersale:return-record:view')")
    public Result<PageResult<AfterSaleReturnRecordDTO>> pageReturnRecords(
            @Valid final AfterSaleReturnRecordQueryVO query) {
        return Result.success(returnRecordService.pageReturnRecords(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('aftersale:return-record:view')")
    public Result<AfterSaleReturnRecordDTO> getReturnRecord(@PathVariable final Long id) {
        return Result.success(returnRecordService.getReturnRecord(id));
    }
}
