package com.mall.product.controller;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.vo.CategoryQueryVO;
import com.mall.common.result.Result;
import com.mall.product.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/product/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:category:view')")
    public Result<List<CategoryDTO>> listCategories(@Valid final CategoryQueryVO query) {
        return Result.success(categoryService.listCategories(query));
    }
}
