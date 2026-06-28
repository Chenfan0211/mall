package com.mall.user.controller;

import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.user.dto.UserReturnRecordDTO;
import com.mall.api.user.vo.UserAfterSaleApplyVO;
import com.mall.api.user.vo.UserAfterSaleQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserAfterSaleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user/after-sales")
public class UserAfterSaleController {

    private final UserAfterSaleService userAfterSaleService;

    public UserAfterSaleController(final UserAfterSaleService userAfterSaleService) {
        this.userAfterSaleService = userAfterSaleService;
    }

    @GetMapping
    public Result<PageResult<AfterSaleDTO>> pageAfterSales(@Valid final UserAfterSaleQueryVO query) {
        return Result.success(userAfterSaleService.pageAfterSales(query));
    }

    @PostMapping
    public Result<AfterSaleDTO> applyAfterSale(@Valid @RequestBody final UserAfterSaleApplyVO request) {
        return Result.success(userAfterSaleService.applyAfterSale(request));
    }

    @GetMapping("/{id}")
    public Result<AfterSaleDTO> getAfterSale(@PathVariable final Long id, @RequestParam final Long userId) {
        return Result.success(userAfterSaleService.getAfterSale(id, userId));
    }

    @GetMapping("/{id}/items")
    public Result<List<AfterSaleItemDTO>> listAfterSaleItems(
            @PathVariable final Long id,
            @RequestParam final Long userId) {
        return Result.success(userAfterSaleService.listAfterSaleItems(id, userId));
    }

    @GetMapping("/{id}/returns")
    public Result<List<UserReturnRecordDTO>> listReturnRecords(
            @PathVariable final Long id,
            @RequestParam final Long userId) {
        return Result.success(userAfterSaleService.listReturnRecords(id, userId));
    }
}
