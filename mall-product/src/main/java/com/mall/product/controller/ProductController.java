package com.mall.product.controller;

import com.mall.api.product.dto.ProductDTO;
import com.mall.api.product.vo.ProductQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/product/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:product:view')")
    public Result<PageResult<ProductDTO>> pageProducts(@Valid final ProductQueryVO query) {
        return Result.success(productService.pageProducts(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:product:view')")
    public Result<ProductDTO> getProduct(@PathVariable final Long id) {
        return Result.success(productService.getProduct(id));
    }
}
