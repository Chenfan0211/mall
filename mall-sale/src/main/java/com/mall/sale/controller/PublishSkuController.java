package com.mall.sale.controller;

import com.mall.api.sale.dto.PublishSkuDTO;
import com.mall.api.sale.vo.PublishSkuQueryVO;
import com.mall.common.result.Result;
import com.mall.sale.service.PublishSkuService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/sale/publish-skus")
public class PublishSkuController {

    private final PublishSkuService publishSkuService;

    public PublishSkuController(final PublishSkuService publishSkuService) {
        this.publishSkuService = publishSkuService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('sale:period:view')")
    public Result<List<PublishSkuDTO>> listPublishSkus(@Valid final PublishSkuQueryVO query) {
        return Result.success(publishSkuService.listPublishSkus(query));
    }
}
