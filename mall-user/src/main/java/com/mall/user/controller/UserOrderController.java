package com.mall.user.controller;

import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.user.vo.UserOrderCancelVO;
import com.mall.api.user.vo.UserOrderQueryVO;
import com.mall.api.user.vo.UserOrderSubmitVO;
import com.mall.api.user.vo.UserPaymentCreateVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserOrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/user/orders")
public class UserOrderController {

    private final UserOrderService userOrderService;

    public UserOrderController(final UserOrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:order:view')")
    public Result<PageResult<OrderDTO>> pageOrders(@Valid final UserOrderQueryVO query) {
        return Result.success(userOrderService.pageOrders(query));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:order:submit')")
    public Result<OrderDTO> submitOrder(@Valid @RequestBody final UserOrderSubmitVO request) {
        return Result.success(userOrderService.submitOrder(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:order:view')")
    public Result<OrderDTO> getOrder(@PathVariable final Long id, @RequestParam final Long userId) {
        return Result.success(userOrderService.getOrder(id, userId));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('user:order:cancel')")
    public Result<OrderDTO> cancelWaitPayOrder(
            @PathVariable final Long id,
            @Valid @RequestBody final UserOrderCancelVO request) {
        return Result.success(userOrderService.cancelWaitPayOrder(id, request));
    }

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('user:order:view')")
    public Result<PageResult<OrderItemDTO>> pageOrderItems(@Valid final UserOrderQueryVO query) {
        return Result.success(userOrderService.pageOrderItems(query));
    }

    @GetMapping("/{id}/tracks")
    @PreAuthorize("hasAuthority('user:order:view')")
    public Result<List<FulfillmentTrackDTO>> listFulfillmentTracks(
            @PathVariable final Long id,
            @RequestParam final Long userId) {
        return Result.success(userOrderService.listFulfillmentTracks(id, userId));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('user:payment:create')")
    public Result<PaymentTradeDTO> createPaymentTrade(
            @PathVariable final Long id,
            @Valid @RequestBody final UserPaymentCreateVO request) {
        return Result.success(userOrderService.createPaymentTrade(id, request));
    }
}
