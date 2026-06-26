package com.mall.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.mall.api.auth.dto.LoginDTO;
import com.mall.api.auth.vo.PasswordLoginVO;
import com.mall.api.system.dto.AccountDTO;
import com.mall.common.exception.BusinessException;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysAccount;
import com.mall.system.service.AccountService;
import com.mall.system.service.DataScopeService;
import com.mall.system.service.MenuService;
import com.mall.system.service.RoleService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private RoleService roleService;

    @Mock
    private MenuService menuService;

    @Mock
    private DataScopeService dataScopeService;

    @Mock
    private SystemConvert systemConvert;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void passwordLogin_success() {
        final PasswordLoginVO loginVO = new PasswordLoginVO();
        loginVO.setUsername("test_platform_admin");
        loginVO.setPassword("Test@123456");
        loginVO.setPortalCode("admin");
        final SysAccount account = new SysAccount();
        account.setId(700001L);
        account.setPasswordHash("hash");
        final AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(700001L);

        when(accountService.findEnabledAccountForLogin("test_platform_admin", "admin")).thenReturn(account);
        when(passwordEncoder.matches("Test@123456", "hash")).thenReturn(true);
        when(systemConvert.toAccountDTO(account)).thenReturn(accountDTO);
        when(roleService.listByAccountId(700001L)).thenReturn(Collections.emptyList());
        when(roleService.listAuthoritiesByAccountId(700001L)).thenReturn(List.of("operation:todo:view"));
        when(menuService.listByAccountIdAndPortal(700001L, "admin")).thenReturn(Collections.emptyList());
        when(dataScopeService.listByAccountId(700001L)).thenReturn(Collections.emptyList());

        final LoginDTO result = authService.passwordLogin(loginVO);

        assertEquals(700001L, result.getAccount().getId());
        assertEquals("Bearer", result.getTokenType());
        assertEquals("operation:todo:view", result.getAuthorities().get(0));
    }

    @Test
    void passwordLogin_passwordMismatch_throwUnauthorized() {
        final PasswordLoginVO loginVO = new PasswordLoginVO();
        loginVO.setUsername("test_platform_admin");
        loginVO.setPassword("wrong");
        loginVO.setPortalCode("admin");
        final SysAccount account = new SysAccount();
        account.setId(700001L);
        account.setPasswordHash("hash");

        when(accountService.findEnabledAccountForLogin("test_platform_admin", "admin")).thenReturn(account);
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.passwordLogin(loginVO));
    }
}
