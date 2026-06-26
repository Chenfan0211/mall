package com.mall.trade.controller;

import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.trade.vo.OrderItemQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.trade.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/trade/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(final OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('trade:order:item:view')")
    public Result<PageResult<OrderItemDTO>> pageOrderItems(@Valid final OrderItemQueryVO query) {
        return Result.success(orderItemService.pageOrderItems(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('trade:order:item:view')")
    public Result<OrderItemDTO> getOrderItem(@PathVariable final Long id) {
        return Result.success(orderItemService.getOrderItem(id));
    }
}
