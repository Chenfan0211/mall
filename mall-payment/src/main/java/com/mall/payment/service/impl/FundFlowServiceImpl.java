package com.mall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.payment.dto.FundFlowDTO;
import com.mall.api.payment.vo.FundFlowQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.payment.convert.PaymentConvert;
import com.mall.payment.entity.PayFundFlow;
import com.mall.payment.mapper.PayFundFlowMapper;
import com.mall.payment.service.FundFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class FundFlowServiceImpl implements FundFlowService {

    private final PayFundFlowMapper fundFlowMapper;
    private final PaymentConvert paymentConvert;

    public FundFlowServiceImpl(final PayFundFlowMapper fundFlowMapper, final PaymentConvert paymentConvert) {
        this.fundFlowMapper = fundFlowMapper;
        this.paymentConvert = paymentConvert;
    }

    @Override
    public PageResult<FundFlowDTO> pageFlows(final FundFlowQueryVO query) {
        final FundFlowQueryVO safeQuery = query == null ? new FundFlowQueryVO() : query;
        final LambdaQueryWrapper<PayFundFlow> wrapper = new LambdaQueryWrapper<PayFundFlow>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(PayFundFlow::getFlowNo, safeQuery.getKeyword())
                        .or()
                        .like(PayFundFlow::getBizNo, safeQuery.getKeyword())
                        .or()
                        .like(PayFundFlow::getChannelFlowNo, safeQuery.getKeyword()))
                .eq(safeQuery.getFlowType() != null, PayFundFlow::getFlowType, safeQuery.getFlowType())
                .eq(StringUtils.hasText(safeQuery.getBizNo()), PayFundFlow::getBizNo, safeQuery.getBizNo())
                .eq(safeQuery.getDirection() != null, PayFundFlow::getDirection, safeQuery.getDirection())
                .eq(safeQuery.getSubjectType() != null, PayFundFlow::getSubjectType, safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, PayFundFlow::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getStatus() != null, PayFundFlow::getStatus, safeQuery.getStatus())
                .orderByDesc(PayFundFlow::getId);
        final Page<PayFundFlow> page = fundFlowMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(paymentConvert::toFundFlowDTO)
                .toList());
    }

    @Override
    public FundFlowDTO getFlow(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "资金流水ID不能为空");
        }
        final PayFundFlow flow = fundFlowMapper.selectById(id);
        if (flow == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "资金流水不存在");
        }
        return paymentConvert.toFundFlowDTO(flow);
    }
}
