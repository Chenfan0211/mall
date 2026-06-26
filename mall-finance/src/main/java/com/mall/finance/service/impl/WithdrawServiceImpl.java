package com.mall.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.DepositRecordDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.finance.vo.DepositRecordQueryVO;
import com.mall.api.finance.vo.WithdrawApplyQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinDepositRecord;
import com.mall.finance.entity.FinWithdrawApply;
import com.mall.finance.mapper.FinDepositRecordMapper;
import com.mall.finance.mapper.FinWithdrawApplyMapper;
import com.mall.finance.service.WithdrawService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class WithdrawServiceImpl implements WithdrawService {

    private final FinWithdrawApplyMapper withdrawApplyMapper;
    private final FinDepositRecordMapper depositRecordMapper;
    private final FinanceConvert financeConvert;

    public WithdrawServiceImpl(
            final FinWithdrawApplyMapper withdrawApplyMapper,
            final FinDepositRecordMapper depositRecordMapper,
            final FinanceConvert financeConvert) {
        this.withdrawApplyMapper = withdrawApplyMapper;
        this.depositRecordMapper = depositRecordMapper;
        this.financeConvert = financeConvert;
    }

    @Override
    public PageResult<WithdrawApplyDTO> pageWithdrawApplies(final WithdrawApplyQueryVO query) {
        final WithdrawApplyQueryVO safeQuery = query == null ? new WithdrawApplyQueryVO() : query;
        final LambdaQueryWrapper<FinWithdrawApply> wrapper = new LambdaQueryWrapper<FinWithdrawApply>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), FinWithdrawApply::getWithdrawNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getAccountId() != null, FinWithdrawApply::getAccountId, safeQuery.getAccountId())
                .eq(safeQuery.getSubjectType() != null, FinWithdrawApply::getSubjectType,
                        safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinWithdrawApply::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getAuditStatus() != null, FinWithdrawApply::getAuditStatus,
                        safeQuery.getAuditStatus())
                .eq(safeQuery.getPayStatus() != null, FinWithdrawApply::getPayStatus, safeQuery.getPayStatus())
                .orderByDesc(FinWithdrawApply::getId);
        final Page<FinWithdrawApply> page = withdrawApplyMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toWithdrawApplyDTO)
                .toList());
    }

    @Override
    public WithdrawApplyDTO getWithdrawApply(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "提现申请ID不能为空");
        }
        final FinWithdrawApply apply = withdrawApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "提现申请不存在");
        }
        return financeConvert.toWithdrawApplyDTO(apply);
    }

    @Override
    public PageResult<DepositRecordDTO> pageDepositRecords(final DepositRecordQueryVO query) {
        final DepositRecordQueryVO safeQuery = query == null ? new DepositRecordQueryVO() : query;
        final LambdaQueryWrapper<FinDepositRecord> wrapper = new LambdaQueryWrapper<FinDepositRecord>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), FinDepositRecord::getDepositNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getAccountId() != null, FinDepositRecord::getAccountId, safeQuery.getAccountId())
                .eq(safeQuery.getSubjectType() != null, FinDepositRecord::getSubjectType,
                        safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinDepositRecord::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getStatus() != null, FinDepositRecord::getStatus, safeQuery.getStatus())
                .orderByDesc(FinDepositRecord::getId);
        final Page<FinDepositRecord> page = depositRecordMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toDepositRecordDTO)
                .toList());
    }

    @Override
    public DepositRecordDTO getDepositRecord(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "押金记录ID不能为空");
        }
        final FinDepositRecord record = depositRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "押金记录不存在");
        }
        return financeConvert.toDepositRecordDTO(record);
    }
}
