package com.mall.station.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.station.dto.StationMessageDTO;
import com.mall.api.station.vo.StationWithdrawApplyVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.station.convert.StationConvert;
import com.mall.station.entity.FinAccount;
import com.mall.station.entity.FinAccountFlow;
import com.mall.station.entity.FinWithdrawApply;
import com.mall.station.entity.MsgRecord;
import com.mall.station.mapper.FinAccountFlowMapper;
import com.mall.station.mapper.FinAccountMapper;
import com.mall.station.mapper.FinCommissionDetailMapper;
import com.mall.station.mapper.FinWithdrawApplyMapper;
import com.mall.station.mapper.MsgRecordMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StationFinanceServiceImplTest {

    @Mock
    private FinAccountMapper accountMapper;

    @Mock
    private FinAccountFlowMapper accountFlowMapper;

    @Mock
    private FinCommissionDetailMapper commissionDetailMapper;

    @Mock
    private FinWithdrawApplyMapper withdrawApplyMapper;

    @Mock
    private MsgRecordMapper msgRecordMapper;

    @Mock
    private StationConvert stationConvert;

    @InjectMocks
    private StationFinanceServiceImpl stationFinanceService;

    @Test
    void pageAccounts_success() {
        final FinAccount account = new FinAccount();
        account.setId(790002L);
        account.setAccountNo("FIN_ACC_STATION");
        final FinanceAccountDTO accountDTO = new FinanceAccountDTO();
        accountDTO.setId(790002L);
        accountDTO.setAccountNo("FIN_ACC_STATION");
        final Page<FinAccount> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(account));
        when(accountMapper.selectPage(any(), any())).thenReturn(page);
        when(stationConvert.toFinanceAccountDTO(account)).thenReturn(accountDTO);

        final PageResult<FinanceAccountDTO> result = stationFinanceService.pageAccounts(null);

        assertEquals(1L, result.getTotal());
        assertEquals("FIN_ACC_STATION", result.getList().get(0).getAccountNo());
    }

    @Test
    void getAccount_notFound_throwNotFound() {
        when(accountMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> stationFinanceService.getAccount(790999L));
    }

    @Test
    void applyWithdraw_success() {
        final StationWithdrawApplyVO request = new StationWithdrawApplyVO();
        request.setAccountId(790002L);
        request.setWithdrawAmount(new java.math.BigDecimal("100.0000"));
        request.setReceiveAccountName("自提点负责人A");
        request.setReceiveAccountNo("622200000001");
        final FinAccount account = new FinAccount();
        account.setId(790002L);
        account.setSubjectType(2L);
        account.setSubjectId(720001L);
        account.setAvailableAmount(new java.math.BigDecimal("800.0000"));
        account.setFrozenAmount(java.math.BigDecimal.ZERO);
        account.setStatus(1L);
        final WithdrawApplyDTO dto = new WithdrawApplyDTO();
        dto.setAccountId(790002L);
        dto.setAuditStatus(10L);
        when(accountMapper.selectById(790002L)).thenReturn(account);
        when(accountMapper.updateById(account)).thenReturn(1);
        when(withdrawApplyMapper.insert(any(FinWithdrawApply.class))).thenReturn(1);
        when(accountFlowMapper.insert(any(FinAccountFlow.class))).thenReturn(1);
        when(stationConvert.toWithdrawApplyDTO(any())).thenReturn(dto);

        final WithdrawApplyDTO result = stationFinanceService.applyWithdraw(request);

        assertEquals(10L, result.getAuditStatus());
        assertEquals(new java.math.BigDecimal("700.0000"), account.getAvailableAmount());
        verify(withdrawApplyMapper).insert(any(FinWithdrawApply.class));
        verify(accountFlowMapper).insert(any(FinAccountFlow.class));
    }

    @Test
    void cancelWithdraw_waitAudit_success() {
        final FinWithdrawApply apply = new FinWithdrawApply();
        apply.setId(790401L);
        apply.setWithdrawNo("WD_TEST_WAIT");
        apply.setAccountId(790002L);
        apply.setWithdrawAmount(new java.math.BigDecimal("100.0000"));
        apply.setAuditStatus(10L);
        final FinAccount account = new FinAccount();
        account.setId(790002L);
        account.setSubjectType(2L);
        account.setSubjectId(720001L);
        account.setAvailableAmount(new java.math.BigDecimal("700.0000"));
        account.setFrozenAmount(new java.math.BigDecimal("100.0000"));
        final WithdrawApplyDTO dto = new WithdrawApplyDTO();
        dto.setId(790401L);
        dto.setAuditStatus(40L);
        when(withdrawApplyMapper.selectById(790401L)).thenReturn(apply);
        when(accountMapper.selectById(790002L)).thenReturn(account);
        when(accountMapper.updateById(account)).thenReturn(1);
        when(withdrawApplyMapper.updateById(apply)).thenReturn(1);
        when(accountFlowMapper.insert(any(FinAccountFlow.class))).thenReturn(1);
        when(stationConvert.toWithdrawApplyDTO(apply)).thenReturn(dto);

        final WithdrawApplyDTO result = stationFinanceService.cancelWithdraw(790401L);

        assertEquals(40L, result.getAuditStatus());
        assertEquals(new java.math.BigDecimal("800.0000"), account.getAvailableAmount());
    }

    @Test
    void markMessageRead_unread_success() {
        final MsgRecord message = new MsgRecord();
        message.setId(792102L);
        message.setReadStatus(0L);
        final StationMessageDTO dto = new StationMessageDTO();
        dto.setId(792102L);
        dto.setReadStatus(1L);
        when(msgRecordMapper.selectById(792102L)).thenReturn(message);
        when(msgRecordMapper.updateById(message)).thenReturn(1);
        when(stationConvert.toStationMessageDTO(message)).thenReturn(dto);

        final StationMessageDTO result = stationFinanceService.markMessageRead(792102L);

        assertEquals(1L, result.getReadStatus());
    }
}
