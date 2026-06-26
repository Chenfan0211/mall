package com.mall.finance.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.DepositRecordDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinDepositRecord;
import com.mall.finance.entity.FinWithdrawApply;
import com.mall.finance.mapper.FinDepositRecordMapper;
import com.mall.finance.mapper.FinWithdrawApplyMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceImplTest {

    @Mock
    private FinWithdrawApplyMapper withdrawApplyMapper;

    @Mock
    private FinDepositRecordMapper depositRecordMapper;

    @Mock
    private FinanceConvert financeConvert;

    @InjectMocks
    private WithdrawServiceImpl withdrawService;

    @Test
    void pageWithdrawApplies_success() {
        final FinWithdrawApply withdrawApply = new FinWithdrawApply();
        withdrawApply.setId(790401L);
        withdrawApply.setWithdrawNo("WD_TEST_WAIT");
        final WithdrawApplyDTO withdrawDTO = new WithdrawApplyDTO();
        withdrawDTO.setId(790401L);
        withdrawDTO.setWithdrawNo("WD_TEST_WAIT");
        final Page<FinWithdrawApply> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(withdrawApply));
        when(withdrawApplyMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toWithdrawApplyDTO(withdrawApply)).thenReturn(withdrawDTO);

        final PageResult<WithdrawApplyDTO> result = withdrawService.pageWithdrawApplies(null);

        assertEquals(1L, result.getTotal());
        assertEquals("WD_TEST_WAIT", result.getList().get(0).getWithdrawNo());
    }

    @Test
    void getWithdrawApply_notFound_throwNotFound() {
        when(withdrawApplyMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> withdrawService.getWithdrawApply(790999L));
    }

    @Test
    void pageDepositRecords_success() {
        final FinDepositRecord depositRecord = new FinDepositRecord();
        depositRecord.setId(790501L);
        depositRecord.setDepositNo("DEP_TEST_FROZEN");
        final DepositRecordDTO depositDTO = new DepositRecordDTO();
        depositDTO.setId(790501L);
        depositDTO.setDepositNo("DEP_TEST_FROZEN");
        final Page<FinDepositRecord> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(depositRecord));
        when(depositRecordMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toDepositRecordDTO(depositRecord)).thenReturn(depositDTO);

        final PageResult<DepositRecordDTO> result = withdrawService.pageDepositRecords(null);

        assertEquals(1L, result.getTotal());
        assertEquals("DEP_TEST_FROZEN", result.getList().get(0).getDepositNo());
    }

    @Test
    void getDepositRecord_notFound_throwNotFound() {
        when(depositRecordMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> withdrawService.getDepositRecord(790999L));
    }
}
