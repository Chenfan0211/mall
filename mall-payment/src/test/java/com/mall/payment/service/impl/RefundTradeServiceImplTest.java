package com.mall.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.payment.dto.RefundTradeDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.payment.convert.PaymentConvert;
import com.mall.payment.entity.PayRefundTrade;
import com.mall.payment.mapper.PayRefundTradeMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefundTradeServiceImplTest {

    @Mock
    private PayRefundTradeMapper refundTradeMapper;

    @Mock
    private PaymentConvert paymentConvert;

    @InjectMocks
    private RefundTradeServiceImpl refundTradeService;

    @Test
    void pageRefunds_success() {
        final PayRefundTrade refund = new PayRefundTrade();
        refund.setId(766001L);
        refund.setRefundNo("RF_TEST_WAIT");
        final RefundTradeDTO refundDTO = new RefundTradeDTO();
        refundDTO.setId(766001L);
        refundDTO.setRefundNo("RF_TEST_WAIT");
        final Page<PayRefundTrade> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(refund));
        when(refundTradeMapper.selectPage(any(), any())).thenReturn(page);
        when(paymentConvert.toRefundTradeDTO(refund)).thenReturn(refundDTO);

        final PageResult<RefundTradeDTO> result = refundTradeService.pageRefunds(null);

        assertEquals(1L, result.getTotal());
        assertEquals("RF_TEST_WAIT", result.getList().get(0).getRefundNo());
    }

    @Test
    void getRefund_notFound_throwNotFound() {
        when(refundTradeMapper.selectById(766999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> refundTradeService.getRefund(766999L));
    }
}
