package com.mall.product.controller;

import com.mall.api.product.dto.SkuDTO;
import com.mall.api.product.dto.StockSpecDTO;
import com.mall.api.product.vo.SkuQueryVO;
import com.mall.common.result.Result;
import com.mall.product.service.SkuService;
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
@RequestMapping("/api/product/skus")
public class SkuController {

    private final SkuService skuService;

    public SkuController(final SkuService skuService) {
        this.skuService = skuService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product:sku:view')")
    public Result<List<SkuDTO>> listSkus(@Valid final SkuQueryVO query) {
        return Result.success(skuService.listSkus(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:sku:view')")
    public Result<SkuDTO> getSku(@PathVariable final Long id) {
        return Result.success(skuService.getSku(id));
    }

    @GetMapping("/{id}/stock-specs")
    @PreAuthorize("hasAuthority('product:sku:view')")
    public Result<List<StockSpecDTO>> listStockSpecs(@PathVariable final Long id) {
        return Result.success(skuService.listStockSpecs(id));
    }
}
