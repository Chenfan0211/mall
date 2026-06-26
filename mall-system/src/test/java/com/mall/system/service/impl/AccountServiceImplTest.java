package com.mall.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.common.exception.BusinessException;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysAccount;
import com.mall.system.mapper.SysAccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private SysAccountMapper accountMapper;

    @Mock
    private SystemConvert systemConvert;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void findEnabledAccountForLogin_success() {
        final SysAccount account = new SysAccount();
        account.setId(1L);
        account.setUsername("test");
        account.setStatus(1L);
        when(accountMapper.selectOne(any())).thenReturn(account);

        final SysAccount result = accountService.findEnabledAccountForLogin("test", "admin");

        assertEquals(1L, result.getId());
    }

    @Test
    void findEnabledAccountForLogin_disabled_throwForbidden() {
        final SysAccount account = new SysAccount();
        account.setId(1L);
        account.setUsername("test");
        account.setStatus(2L);
        when(accountMapper.selectOne(any())).thenReturn(account);

        assertThrows(BusinessException.class, () -> accountService.findEnabledAccountForLogin("test", "admin"));
    }

    @Test
    void findEnabledAccountForLogin_notFound_throwUnauthorized() {
        when(accountMapper.selectOne(any())).thenReturn(null);

        assertThrows(BusinessException.class, () -> accountService.findEnabledAccountForLogin("test", "admin"));
    }
}
