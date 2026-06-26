package com.mall.trade.controller;

import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderStatusLogDTO;
import com.mall.api.trade.vo.OrderQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.trade.service.OrderService;
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
@RequestMapping("/api/trade/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('trade:order:view')")
    public Result<PageResult<OrderDTO>> pageOrders(@Valid final OrderQueryVO query) {
        return Result.success(orderService.pageOrders(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('trade:order:view')")
    public Result<OrderDTO> getOrder(@PathVariable final Long id) {
        return Result.success(orderService.getOrder(id));
    }

    @GetMapping("/{id}/status-logs")
    @PreAuthorize("hasAuthority('trade:order:view')")
    public Result<List<OrderStatusLogDTO>> listStatusLogs(@PathVariable final Long id) {
        return Result.success(orderService.listStatusLogs(id));
    }
}
