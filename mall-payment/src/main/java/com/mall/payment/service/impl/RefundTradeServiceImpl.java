package com.mall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.payment.dto.RefundTradeDTO;
import com.mall.api.payment.vo.RefundTradeQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.payment.convert.PaymentConvert;
import com.mall.payment.entity.PayRefundTrade;
import com.mall.payment.mapper.PayRefundTradeMapper;
import com.mall.payment.service.RefundTradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class RefundTradeServiceImpl implements RefundTradeService {

    private final PayRefundTradeMapper refundTradeMapper;
    private final PaymentConvert paymentConvert;

    public RefundTradeServiceImpl(
            final PayRefundTradeMapper refundTradeMapper,
            final PaymentConvert paymentConvert) {
        this.refundTradeMapper = refundTradeMapper;
        this.paymentConvert = paymentConvert;
    }

    @Override
    public PageResult<RefundTradeDTO> pageRefunds(final RefundTradeQueryVO query) {
        final RefundTradeQueryVO safeQuery = query == null ? new RefundTradeQueryVO() : query;
        final LambdaQueryWrapper<PayRefundTrade> wrapper = new LambdaQueryWrapper<PayRefundTrade>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(PayRefundTrade::getRefundNo, safeQuery.getKeyword())
                        .or()
                        .like(PayRefundTrade::getPayNo, safeQuery.getKeyword())
                        .or()
                        .like(PayRefundTrade::getChannelRefundNo, safeQuery.getKeyword()))
                .eq(StringUtils.hasText(safeQuery.getPayNo()), PayRefundTrade::getPayNo, safeQuery.getPayNo())
                .eq(safeQuery.getOrderId() != null, PayRefundTrade::getOrderId, safeQuery.getOrderId())
                .eq(safeQuery.getAfterSaleId() != null, PayRefundTrade::getAfterSaleId, safeQuery.getAfterSaleId())
                .eq(safeQuery.getRefundStatus() != null, PayRefundTrade::getRefundStatus,
                        safeQuery.getRefundStatus())
                .orderByDesc(PayRefundTrade::getId);
        final Page<PayRefundTrade> page = refundTradeMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(paymentConvert::toRefundTradeDTO)
                .toList());
    }

    @Override
    public RefundTradeDTO getRefund(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "退款单ID不能为空");
        }
        final PayRefundTrade refund = refundTradeMapper.selectById(id);
        if (refund == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "退款单不存在");
        }
        return paymentConvert.toRefundTradeDTO(refund);
    }
}
