package com.mall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.payment.vo.PaymentTradeQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.payment.convert.PaymentConvert;
import com.mall.payment.entity.PayTrade;
import com.mall.payment.mapper.PayTradeMapper;
import com.mall.payment.service.PaymentTradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class PaymentTradeServiceImpl implements PaymentTradeService {

    private final PayTradeMapper payTradeMapper;
    private final PaymentConvert paymentConvert;

    public PaymentTradeServiceImpl(final PayTradeMapper payTradeMapper, final PaymentConvert paymentConvert) {
        this.payTradeMapper = payTradeMapper;
        this.paymentConvert = paymentConvert;
    }

    @Override
    public PageResult<PaymentTradeDTO> pageTrades(final PaymentTradeQueryVO query) {
        final PaymentTradeQueryVO safeQuery = query == null ? new PaymentTradeQueryVO() : query;
        final LambdaQueryWrapper<PayTrade> wrapper = new LambdaQueryWrapper<PayTrade>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(PayTrade::getPayNo, safeQuery.getKeyword())
                        .or()
                        .like(PayTrade::getOrderNo, safeQuery.getKeyword())
                        .or()
                        .like(PayTrade::getChannelTradeNo, safeQuery.getKeyword()))
                .eq(safeQuery.getOrderId() != null, PayTrade::getOrderId, safeQuery.getOrderId())
                .eq(StringUtils.hasText(safeQuery.getOrderNo()), PayTrade::getOrderNo, safeQuery.getOrderNo())
                .eq(safeQuery.getChannelType() != null, PayTrade::getChannelType, safeQuery.getChannelType())
                .eq(safeQuery.getTradeStatus() != null, PayTrade::getTradeStatus, safeQuery.getTradeStatus())
                .orderByDesc(PayTrade::getId);
        final Page<PayTrade> page = payTradeMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(paymentConvert::toPaymentTradeDTO)
                .toList());
    }

    @Override
    public PaymentTradeDTO getTrade(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "支付单ID不能为空");
        }
        final PayTrade trade = payTradeMapper.selectById(id);
        if (trade == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "支付单不存在");
        }
        return paymentConvert.toPaymentTradeDTO(trade);
    }
}
