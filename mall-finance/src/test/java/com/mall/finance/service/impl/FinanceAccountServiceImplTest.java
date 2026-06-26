package com.mall.finance.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinAccount;
import com.mall.finance.entity.FinAccountFlow;
import com.mall.finance.mapper.FinAccountFlowMapper;
import com.mall.finance.mapper.FinAccountMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinanceAccountServiceImplTest {

    @Mock
    private FinAccountMapper accountMapper;

    @Mock
    private FinAccountFlowMapper accountFlowMapper;

    @Mock
    private FinanceConvert financeConvert;

    @InjectMocks
    private FinanceAccountServiceImpl financeAccountService;

    @Test
    void pageAccounts_success() {
        final FinAccount account = new FinAccount();
        account.setId(790001L);
        account.setAccountNo("FIN_ACC_SUPPLIER");
        final FinanceAccountDTO accountDTO = new FinanceAccountDTO();
        accountDTO.setId(790001L);
        accountDTO.setAccountNo("FIN_ACC_SUPPLIER");
        final Page<FinAccount> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(account));
        when(accountMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toFinanceAccountDTO(account)).thenReturn(accountDTO);

        final PageResult<FinanceAccountDTO> result = financeAccountService.pageAccounts(null);

        assertEquals(1L, result.getTotal());
        assertEquals("FIN_ACC_SUPPLIER", result.getList().get(0).getAccountNo());
    }

    @Test
    void getAccount_notFound_throwNotFound() {
        when(accountMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> financeAccountService.getAccount(790999L));
    }

    @Test
    void pageAccountFlows_success() {
        final FinAccountFlow flow = new FinAccountFlow();
        flow.setId(790101L);
        flow.setFlowNo("ACC_FLOW_COMMISSION");
        final FinanceAccountFlowDTO flowDTO = new FinanceAccountFlowDTO();
        flowDTO.setId(790101L);
        flowDTO.setFlowNo("ACC_FLOW_COMMISSION");
        final Page<FinAccountFlow> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(flow));
        when(accountFlowMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toFinanceAccountFlowDTO(flow)).thenReturn(flowDTO);

        final PageResult<FinanceAccountFlowDTO> result = financeAccountService.pageAccountFlows(null);

        assertEquals(1L, result.getTotal());
        assertEquals("ACC_FLOW_COMMISSION", result.getList().get(0).getFlowNo());
    }

    @Test
    void getAccountFlow_notFound_throwNotFound() {
        when(accountFlowMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> financeAccountService.getAccountFlow(790999L));
    }
}
