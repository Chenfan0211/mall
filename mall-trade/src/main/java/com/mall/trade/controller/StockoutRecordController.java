package com.mall.trade.controller;

import com.mall.api.trade.dto.StockoutRecordDTO;
import com.mall.api.trade.vo.StockoutRecordQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.trade.service.StockoutRecordService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/trade/stockouts")
public class StockoutRecordController {

    private final StockoutRecordService stockoutRecordService;

    public StockoutRecordController(final StockoutRecordService stockoutRecordService) {
        this.stockoutRecordService = stockoutRecordService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('trade:stockout:view')")
    public Result<PageResult<StockoutRecordDTO>> pageStockouts(@Valid final StockoutRecordQueryVO query) {
        return Result.success(stockoutRecordService.pageStockouts(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('trade:stockout:view')")
    public Result<StockoutRecordDTO> getStockout(@PathVariable final Long id) {
        return Result.success(stockoutRecordService.getStockout(id));
    }
}
