package com.mall.user.controller;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.api.user.vo.UserProductQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('user:product:view')")
    public Result<PageResult<CategoryDTO>> pageCategories(@Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pageCategories(query));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:product:view')")
    public Result<PageResult<UserProductCardDTO>> pageProducts(@Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pageProducts(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:product:view')")
    public Result<UserProductDetailDTO> getProductDetail(
            @PathVariable final Long id,
            @Valid final UserProductQueryVO query) {
        return Result.success(userProductService.getProductDetail(id, query));
    }

    @GetMapping("/comments")
    @PreAuthorize("hasAuthority('user:product:view')")
    public Result<PageResult<UserCommentDTO>> pageComments(@Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pageComments(query));
    }
}
