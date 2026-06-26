package com.mall.supplier.controller;

import com.mall.api.supplier.dto.DeliveryItemDTO;
import com.mall.api.supplier.dto.DeliveryOrderDTO;
import com.mall.api.supplier.vo.DeliveryItemQueryVO;
import com.mall.api.supplier.vo.DeliveryOrderQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.supplier.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/supplier/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(final DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('delivery:order:view')")
    public Result<PageResult<DeliveryOrderDTO>> pageDeliveryOrders(@Valid final DeliveryOrderQueryVO query) {
        return Result.success(deliveryService.pageDeliveryOrders(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('delivery:order:view')")
    public Result<DeliveryOrderDTO> getDeliveryOrder(@PathVariable final Long id) {
        return Result.success(deliveryService.getDeliveryOrder(id));
    }

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('delivery:item:view')")
    public Result<PageResult<DeliveryItemDTO>> pageDeliveryItems(@Valid final DeliveryItemQueryVO query) {
        return Result.success(deliveryService.pageDeliveryItems(query));
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("hasAuthority('delivery:item:view')")
    public Result<DeliveryItemDTO> getDeliveryItem(@PathVariable final Long id) {
        return Result.success(deliveryService.getDeliveryItem(id));
    }
}
