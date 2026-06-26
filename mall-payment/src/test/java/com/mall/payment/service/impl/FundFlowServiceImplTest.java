package com.mall.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.payment.dto.FundFlowDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.payment.convert.PaymentConvert;
import com.mall.payment.entity.PayFundFlow;
import com.mall.payment.mapper.PayFundFlowMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FundFlowServiceImplTest {

    @Mock
    private PayFundFlowMapper fundFlowMapper;

    @Mock
    private PaymentConvert paymentConvert;

    @InjectMocks
    private FundFlowServiceImpl fundFlowService;

    @Test
    void pageFlows_success() {
        final PayFundFlow flow = new PayFundFlow();
        flow.setId(766101L);
        flow.setFlowNo("FUND_TEST_PAY");
        final FundFlowDTO flowDTO = new FundFlowDTO();
        flowDTO.setId(766101L);
        flowDTO.setFlowNo("FUND_TEST_PAY");
        final Page<PayFundFlow> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(flow));
        when(fundFlowMapper.selectPage(any(), any())).thenReturn(page);
        when(paymentConvert.toFundFlowDTO(flow)).thenReturn(flowDTO);

        final PageResult<FundFlowDTO> result = fundFlowService.pageFlows(null);

        assertEquals(1L, result.getTotal());
        assertEquals("FUND_TEST_PAY", result.getList().get(0).getFlowNo());
    }

    @Test
    void getFlow_notFound_throwNotFound() {
        when(fundFlowMapper.selectById(766999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> fundFlowService.getFlow(766999L));
    }
}
