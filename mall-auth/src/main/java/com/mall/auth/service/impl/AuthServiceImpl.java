package com.mall.auth.service.impl;

import com.mall.api.auth.dto.LoginDTO;
import com.mall.api.auth.vo.PasswordLoginVO;
import com.mall.api.system.dto.AccountDTO;
import com.mall.auth.service.AuthService;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysAccount;
import com.mall.system.service.AccountService;
import com.mall.system.service.DataScopeService;
import com.mall.system.service.MenuService;
import com.mall.system.service.RoleService;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final Long TOKEN_EXPIRES_SECONDS = 7200L;

    private final AccountService accountService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final DataScopeService dataScopeService;
    private final SystemConvert systemConvert;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            final AccountService accountService,
            final RoleService roleService,
            final MenuService menuService,
            final DataScopeService dataScopeService,
            final SystemConvert systemConvert,
            final PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.roleService = roleService;
        this.menuService = menuService;
        this.dataScopeService = dataScopeService;
        this.systemConvert = systemConvert;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginDTO passwordLogin(final PasswordLoginVO loginVO) {
        final SysAccount account = accountService.findEnabledAccountForLogin(
                loginVO.getUsername(), loginVO.getPortalCode());
        if (!passwordEncoder.matches(loginVO.getPassword(), account.getPasswordHash())) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        final AccountDTO accountDTO = systemConvert.toAccountDTO(account);
        final LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
        loginDTO.setTokenType("Bearer");
        loginDTO.setExpiresIn(TOKEN_EXPIRES_SECONDS);
        loginDTO.setAccount(accountDTO);
        loginDTO.setRoles(roleService.listByAccountId(account.getId()));
        loginDTO.setMenus(menuService.listByAccountIdAndPortal(account.getId(), loginVO.getPortalCode()));
        loginDTO.setDataScopes(dataScopeService.listByAccountId(account.getId()));
        loginDTO.setAuthorities(roleService.listAuthoritiesByAccountId(account.getId()));
        return loginDTO;
    }
}
