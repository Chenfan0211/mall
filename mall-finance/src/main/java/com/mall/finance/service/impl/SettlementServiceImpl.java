package com.mall.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.ReconciliationBatchDTO;
import com.mall.api.finance.dto.SupplierSettlementDTO;
import com.mall.api.finance.vo.ReconciliationBatchQueryVO;
import com.mall.api.finance.vo.SupplierSettlementQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinReconciliationBatch;
import com.mall.finance.entity.FinSupplierSettlement;
import com.mall.finance.mapper.FinReconciliationBatchMapper;
import com.mall.finance.mapper.FinSupplierSettlementMapper;
import com.mall.finance.service.SettlementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class SettlementServiceImpl implements SettlementService {

    private final FinSupplierSettlementMapper supplierSettlementMapper;
    private final FinReconciliationBatchMapper reconciliationBatchMapper;
    private final FinanceConvert financeConvert;

    public SettlementServiceImpl(
            final FinSupplierSettlementMapper supplierSettlementMapper,
            final FinReconciliationBatchMapper reconciliationBatchMapper,
            final FinanceConvert financeConvert) {
        this.supplierSettlementMapper = supplierSettlementMapper;
        this.reconciliationBatchMapper = reconciliationBatchMapper;
        this.financeConvert = financeConvert;
    }

    @Override
    public PageResult<SupplierSettlementDTO> pageSupplierSettlements(final SupplierSettlementQueryVO query) {
        final SupplierSettlementQueryVO safeQuery = query == null ? new SupplierSettlementQueryVO() : query;
        final LambdaQueryWrapper<FinSupplierSettlement> wrapper =
                new LambdaQueryWrapper<FinSupplierSettlement>()
                        .like(StringUtils.hasText(safeQuery.getKeyword()),
                                FinSupplierSettlement::getSettlementNo, safeQuery.getKeyword())
                        .eq(safeQuery.getSupplierId() != null, FinSupplierSettlement::getSupplierId,
                                safeQuery.getSupplierId())
                        .eq(safeQuery.getStatus() != null, FinSupplierSettlement::getStatus, safeQuery.getStatus())
                        .ge(safeQuery.getPeriodStartDate() != null, FinSupplierSettlement::getPeriodStartDate,
                                safeQuery.getPeriodStartDate())
                        .le(safeQuery.getPeriodEndDate() != null, FinSupplierSettlement::getPeriodEndDate,
                                safeQuery.getPeriodEndDate())
                        .orderByDesc(FinSupplierSettlement::getId);
        final Page<FinSupplierSettlement> page = supplierSettlementMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toSupplierSettlementDTO)
                .toList());
    }

    @Override
    public SupplierSettlementDTO getSupplierSettlement(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "供应商结算单ID不能为空");
        }
        final FinSupplierSettlement settlement = supplierSettlementMapper.selectById(id);
        if (settlement == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "供应商结算单不存在");
        }
        return financeConvert.toSupplierSettlementDTO(settlement);
    }

    @Override
    public PageResult<ReconciliationBatchDTO> pageReconciliationBatches(final ReconciliationBatchQueryVO query) {
        final ReconciliationBatchQueryVO safeQuery = query == null ? new ReconciliationBatchQueryVO() : query;
        final LambdaQueryWrapper<FinReconciliationBatch> wrapper =
                new LambdaQueryWrapper<FinReconciliationBatch>()
                        .like(StringUtils.hasText(safeQuery.getKeyword()),
                                FinReconciliationBatch::getReconcileNo, safeQuery.getKeyword())
                        .eq(safeQuery.getChannelType() != null, FinReconciliationBatch::getChannelType,
                                safeQuery.getChannelType())
                        .eq(safeQuery.getReconcileDate() != null, FinReconciliationBatch::getReconcileDate,
                                safeQuery.getReconcileDate())
                        .eq(safeQuery.getStatus() != null, FinReconciliationBatch::getStatus, safeQuery.getStatus())
                        .orderByDesc(FinReconciliationBatch::getId);
        final Page<FinReconciliationBatch> page = reconciliationBatchMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toReconciliationBatchDTO)
                .toList());
    }

    @Override
    public ReconciliationBatchDTO getReconciliationBatch(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "对账批次ID不能为空");
        }
        final FinReconciliationBatch batch = reconciliationBatchMapper.selectById(id);
        if (batch == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "对账批次不存在");
        }
        return financeConvert.toReconciliationBatchDTO(batch);
    }
}
