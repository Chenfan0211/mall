package com.mall.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.system.dto.AccountDTO;
import com.mall.api.system.vo.AccountQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysAccount;
import com.mall.system.mapper.SysAccountMapper;
import com.mall.system.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private static final Long ACCOUNT_STATUS_ENABLED = 1L;

    private final SysAccountMapper accountMapper;
    private final SystemConvert systemConvert;

    public AccountServiceImpl(final SysAccountMapper accountMapper, final SystemConvert systemConvert) {
        this.accountMapper = accountMapper;
        this.systemConvert = systemConvert;
    }

    @Override
    public PageResult<AccountDTO> pageAccounts(final AccountQueryVO query) {
        final AccountQueryVO safeQuery = query == null ? new AccountQueryVO() : query;
        final LambdaQueryWrapper<SysAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(safeQuery.getStatus() != null, SysAccount::getStatus, safeQuery.getStatus())
                .eq(StringUtils.hasText(safeQuery.getLoginPortalCode()), SysAccount::getLoginPortalCode,
                        safeQuery.getLoginPortalCode())
                .eq(safeQuery.getAccountType() != null, SysAccount::getAccountType, safeQuery.getAccountType())
                .and(StringUtils.hasText(safeQuery.getKeyword()), condition -> condition
                        .like(SysAccount::getUsername, safeQuery.getKeyword())
                        .or()
                        .like(SysAccount::getMobile, safeQuery.getKeyword())
                        .or()
                        .like(SysAccount::getAccountNo, safeQuery.getKeyword()))
                .orderByDesc(SysAccount::getId);
        final Page<SysAccount> page = accountMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream().map(systemConvert::toAccountDTO).toList());
    }

    @Override
    public AccountDTO getAccount(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "账号ID不能为空");
        }
        final SysAccount account = accountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "账号不存在");
        }
        return systemConvert.toAccountDTO(account);
    }

    @Override
    public SysAccount findEnabledAccountForLogin(final String username, final String portalCode) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(portalCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "账号和登录端不能为空");
        }
        final LambdaQueryWrapper<SysAccount> wrapper = new LambdaQueryWrapper<SysAccount>()
                .eq(SysAccount::getUsername, username)
                .eq(SysAccount::getLoginPortalCode, portalCode);
        final SysAccount account = accountMapper.selectOne(wrapper);
        if (account == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        if (!ACCOUNT_STATUS_ENABLED.equals(account.getStatus())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN, "账号已禁用或锁定");
        }
        return account;
    }
}
