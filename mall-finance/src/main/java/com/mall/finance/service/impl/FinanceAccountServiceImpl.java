package com.mall.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.vo.FinanceAccountFlowQueryVO;
import com.mall.api.finance.vo.FinanceAccountQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinAccount;
import com.mall.finance.entity.FinAccountFlow;
import com.mall.finance.mapper.FinAccountFlowMapper;
import com.mall.finance.mapper.FinAccountMapper;
import com.mall.finance.service.FinanceAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class FinanceAccountServiceImpl implements FinanceAccountService {

    private final FinAccountMapper accountMapper;
    private final FinAccountFlowMapper accountFlowMapper;
    private final FinanceConvert financeConvert;

    public FinanceAccountServiceImpl(
            final FinAccountMapper accountMapper,
            final FinAccountFlowMapper accountFlowMapper,
            final FinanceConvert financeConvert) {
        this.accountMapper = accountMapper;
        this.accountFlowMapper = accountFlowMapper;
        this.financeConvert = financeConvert;
    }

    @Override
    public PageResult<FinanceAccountDTO> pageAccounts(final FinanceAccountQueryVO query) {
        final FinanceAccountQueryVO safeQuery = query == null ? new FinanceAccountQueryVO() : query;
        final LambdaQueryWrapper<FinAccount> wrapper = new LambdaQueryWrapper<FinAccount>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), FinAccount::getAccountNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getSubjectType() != null, FinAccount::getSubjectType, safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinAccount::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getStatus() != null, FinAccount::getStatus, safeQuery.getStatus())
                .orderByDesc(FinAccount::getId);
        final Page<FinAccount> page = accountMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toFinanceAccountDTO)
                .toList());
    }

    @Override
    public FinanceAccountDTO getAccount(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "财务账户ID不能为空");
        }
        final FinAccount account = accountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "财务账户不存在");
        }
        return financeConvert.toFinanceAccountDTO(account);
    }

    @Override
    public PageResult<FinanceAccountFlowDTO> pageAccountFlows(final FinanceAccountFlowQueryVO query) {
        final FinanceAccountFlowQueryVO safeQuery = query == null ? new FinanceAccountFlowQueryVO() : query;
        final LambdaQueryWrapper<FinAccountFlow> wrapper = new LambdaQueryWrapper<FinAccountFlow>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(FinAccountFlow::getFlowNo, safeQuery.getKeyword())
                        .or()
                        .like(FinAccountFlow::getBizNo, safeQuery.getKeyword()))
                .eq(safeQuery.getAccountId() != null, FinAccountFlow::getAccountId, safeQuery.getAccountId())
                .eq(safeQuery.getSubjectType() != null, FinAccountFlow::getSubjectType,
                        safeQuery.getSubjectType())
                .eq(safeQuery.getSubjectId() != null, FinAccountFlow::getSubjectId, safeQuery.getSubjectId())
                .eq(safeQuery.getFlowType() != null, FinAccountFlow::getFlowType, safeQuery.getFlowType())
                .eq(safeQuery.getDirection() != null, FinAccountFlow::getDirection, safeQuery.getDirection())
                .eq(StringUtils.hasText(safeQuery.getBizNo()), FinAccountFlow::getBizNo, safeQuery.getBizNo())
                .orderByDesc(FinAccountFlow::getId);
        final Page<FinAccountFlow> page = accountFlowMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(financeConvert::toFinanceAccountFlowDTO)
                .toList());
    }

    @Override
    public FinanceAccountFlowDTO getAccountFlow(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "账户流水ID不能为空");
        }
        final FinAccountFlow accountFlow = accountFlowMapper.selectById(id);
        if (accountFlow == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "账户流水不存在");
        }
        return financeConvert.toFinanceAccountFlowDTO(accountFlow);
    }
}
