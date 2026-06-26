package com.mall.payment.controller;

import com.mall.api.payment.dto.RefundTradeDTO;
import com.mall.api.payment.vo.RefundTradeQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.payment.service.RefundTradeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/payment/refunds")
public class RefundTradeController {

    private final RefundTradeService refundTradeService;

    public RefundTradeController(final RefundTradeService refundTradeService) {
        this.refundTradeService = refundTradeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('payment:refund:view')")
    public Result<PageResult<RefundTradeDTO>> pageRefunds(@Valid final RefundTradeQueryVO query) {
        return Result.success(refundTradeService.pageRefunds(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:refund:view')")
    public Result<RefundTradeDTO> getRefund(@PathVariable final Long id) {
        return Result.success(refundTradeService.getRefund(id));
    }
}
