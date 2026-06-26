package com.mall.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.SplitFlowDTO;
import com.mall.api.finance.vo.CommissionDetailQueryVO;
import com.mall.api.finance.vo.SplitFlowQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinCommissionDetail;
import com.mall.finance.entity.FinSplitFlow;
import com.mall.finance.mapper.FinCommissionDetailMapper;
import com.mall.finance.mapper.FinSplitFlowMapper;
import com.mall.finance.service.CommissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class CommissionServiceImpl implements CommissionService {

    private final FinCommissionDetailMapper commissionDetailMapper;
    private final FinSplitFlowMapper splitFlowMapper;
    private final FinanceConvert financeConvert;

    public CommissionServiceImpl(
            final FinCommissionDetailMapper commissionDetailMapper,
            final FinSplitFlowMapper splitFlowMapper,
            final FinanceConvert financeConvert) {
        this.commissionDetailMapper = commissionDetailMapper;
        this.splitFlowMapper = splitFlowMapper;
        this.financeConvert = financeConvert;
    }

    @Override
    public PageResult<CommissionDetailDTO> pageCommissionDetails(final CommissionDetailQueryVO query) {
        final CommissionDetailQueryVO safeQuery = query == null ? new CommissionDetailQueryVO() : query;
        final LambdaQueryWrapper<FinCommissionDetail> wrapper = new LambdaQueryWrapper<FinCommissionDetail>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), FinCommissionDetail::getCommissionNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getOrderId() != null, FinCommissionDetail::getOrderId, safeQuery.getOrderId())
                .eq(safeQuery.getOrderItemId() != null, FinCommissionDetail::getOrderItemId,
                        safeQuery.getOrderItemId())
                .eq(safeQuery.getSubjectType() != null, FinCommissionDetail::getSubjectType,
                        safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinCommissionDetail::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getSupplierId() != null, FinCommissionDetail::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getStationId() != null, FinCommissionDetail::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getStatus() != null, FinCommissionDetail::getStatus, safeQuery.getStatus())
                .orderByDesc(FinCommissionDetail::getId);
        final Page<FinCommissionDetail> page = commissionDetailMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toCommissionDetailDTO)
                .toList());
    }

    @Override
    public CommissionDetailDTO getCommissionDetail(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "佣金明细ID不能为空");
        }
        final FinCommissionDetail detail = commissionDetailMapper.selectById(id);
        if (detail == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "佣金明细不存在");
        }
        return financeConvert.toCommissionDetailDTO(detail);
    }

    @Override
    public PageResult<SplitFlowDTO> pageSplitFlows(final SplitFlowQueryVO query) {
        final SplitFlowQueryVO safeQuery = query == null ? new SplitFlowQueryVO() : query;
        final LambdaQueryWrapper<FinSplitFlow> wrapper = new LambdaQueryWrapper<FinSplitFlow>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(FinSplitFlow::getSplitNo, safeQuery.getKeyword())
                        .or()
                        .like(FinSplitFlow::getIdempotentKey, safeQuery.getKeyword()))
                .eq(safeQuery.getCommissionId() != null, FinSplitFlow::getCommissionId,
                        safeQuery.getCommissionId())
                .eq(safeQuery.getOrderId() != null, FinSplitFlow::getOrderId, safeQuery.getOrderId())
                .eq(safeQuery.getSubjectType() != null, FinSplitFlow::getSubjectType, safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinSplitFlow::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getSplitStatus() != null, FinSplitFlow::getSplitStatus, safeQuery.getSplitStatus())
                .orderByDesc(FinSplitFlow::getId);
        final Page<FinSplitFlow> page = splitFlowMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toSplitFlowDTO)
                .toList());
    }

    @Override
    public SplitFlowDTO getSplitFlow(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "分账流水ID不能为空");
        }
        final FinSplitFlow splitFlow = splitFlowMapper.selectById(id);
        if (splitFlow == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "分账流水不存在");
        }
        return financeConvert.toSplitFlowDTO(splitFlow);
    }
}
