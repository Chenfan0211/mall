package com.mall.finance.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.SplitFlowDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinCommissionDetail;
import com.mall.finance.entity.FinSplitFlow;
import com.mall.finance.mapper.FinCommissionDetailMapper;
import com.mall.finance.mapper.FinSplitFlowMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommissionServiceImplTest {

    @Mock
    private FinCommissionDetailMapper commissionDetailMapper;

    @Mock
    private FinSplitFlowMapper splitFlowMapper;

    @Mock
    private FinanceConvert financeConvert;

    @InjectMocks
    private CommissionServiceImpl commissionService;

    @Test
    void pageCommissionDetails_success() {
        final FinCommissionDetail detail = new FinCommissionDetail();
        detail.setId(790201L);
        detail.setCommissionNo("COM_TEST_WAIT_CALC");
        final CommissionDetailDTO detailDTO = new CommissionDetailDTO();
        detailDTO.setId(790201L);
        detailDTO.setCommissionNo("COM_TEST_WAIT_CALC");
        final Page<FinCommissionDetail> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(detail));
        when(commissionDetailMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toCommissionDetailDTO(detail)).thenReturn(detailDTO);

        final PageResult<CommissionDetailDTO> result = commissionService.pageCommissionDetails(null);

        assertEquals(1L, result.getTotal());
        assertEquals("COM_TEST_WAIT_CALC", result.getList().get(0).getCommissionNo());
    }

    @Test
    void getCommissionDetail_notFound_throwNotFound() {
        when(commissionDetailMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> commissionService.getCommissionDetail(790999L));
    }

    @Test
    void pageSplitFlows_success() {
        final FinSplitFlow splitFlow = new FinSplitFlow();
        splitFlow.setId(790301L);
        splitFlow.setSplitNo("SPLIT_TEST_WAIT");
        final SplitFlowDTO splitDTO = new SplitFlowDTO();
        splitDTO.setId(790301L);
        splitDTO.setSplitNo("SPLIT_TEST_WAIT");
        final Page<FinSplitFlow> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(splitFlow));
        when(splitFlowMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toSplitFlowDTO(splitFlow)).thenReturn(splitDTO);

        final PageResult<SplitFlowDTO> result = commissionService.pageSplitFlows(null);

        assertEquals(1L, result.getTotal());
        assertEquals("SPLIT_TEST_WAIT", result.getList().get(0).getSplitNo());
    }

    @Test
    void getSplitFlow_notFound_throwNotFound() {
        when(splitFlowMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> commissionService.getSplitFlow(790999L));
    }
}
