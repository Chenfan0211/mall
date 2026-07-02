package com.mall.user.controller;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.api.user.dto.UserProductPurchaseRecordDTO;
import com.mall.api.user.dto.UserProductReviewStatsDTO;
import com.mall.api.user.vo.UserProductQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserProductService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user/products")
public class UserProductController {

    private final UserProductService userProductService;

    public UserProductController(final UserProductService userProductService) {
        this.userProductService = userProductService;
    }

    @GetMapping("/categories")
    public Result<PageResult<CategoryDTO>> pageCategories(@Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pageCategories(query));
    }

    @GetMapping
    public Result<PageResult<UserProductCardDTO>> pageProducts(@Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pageProducts(query));
    }

    @GetMapping("/{id}")
    public Result<UserProductDetailDTO> getProductDetail(
            @PathVariable final Long id,
            @Valid final UserProductQueryVO query) {
        return Result.success(userProductService.getProductDetail(id, query));
    }

    @GetMapping("/{id}/review-stats")
    public Result<UserProductReviewStatsDTO> getReviewStats(
            @PathVariable final Long id,
            @Valid final UserProductQueryVO query) {
        return Result.success(userProductService.getReviewStats(id, query));
    }

    @GetMapping("/{id}/purchase-records")
    public Result<PageResult<UserProductPurchaseRecordDTO>> pagePurchaseRecords(
            @PathVariable final Long id,
            @Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pagePurchaseRecords(id, query));
    }

    @GetMapping("/comments")
    public Result<PageResult<UserCommentDTO>> pageComments(@Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pageComments(query));
    }
}
