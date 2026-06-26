package com.mall.payment.controller;

import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.payment.vo.PaymentTradeQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.payment.service.PaymentTradeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/payment/trades")
public class PaymentTradeController {

    private final PaymentTradeService paymentTradeService;

    public PaymentTradeController(final PaymentTradeService paymentTradeService) {
        this.paymentTradeService = paymentTradeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('payment:trade:view')")
    public Result<PageResult<PaymentTradeDTO>> pageTrades(@Valid final PaymentTradeQueryVO query) {
        return Result.success(paymentTradeService.pageTrades(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:trade:view')")
    public Result<PaymentTradeDTO> getTrade(@PathVariable final Long id) {
        return Result.success(paymentTradeService.getTrade(id));
    }
}
