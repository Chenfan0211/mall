package com.mall.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.payment.convert.PaymentConvert;
import com.mall.payment.entity.PayTrade;
import com.mall.payment.mapper.PayTradeMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentTradeServiceImplTest {

    @Mock
    private PayTradeMapper payTradeMapper;

    @Mock
    private PaymentConvert paymentConvert;

    @InjectMocks
    private PaymentTradeServiceImpl paymentTradeService;

    @Test
    void pageTrades_success() {
        final PayTrade trade = new PayTrade();
        trade.setId(765001L);
        trade.setPayNo("PAY_TEST_WAIT");
        final PaymentTradeDTO tradeDTO = new PaymentTradeDTO();
        tradeDTO.setId(765001L);
        tradeDTO.setPayNo("PAY_TEST_WAIT");
        final Page<PayTrade> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(trade));
        when(payTradeMapper.selectPage(any(), any())).thenReturn(page);
        when(paymentConvert.toPaymentTradeDTO(trade)).thenReturn(tradeDTO);

        final PageResult<PaymentTradeDTO> result = paymentTradeService.pageTrades(null);

        assertEquals(1L, result.getTotal());
        assertEquals("PAY_TEST_WAIT", result.getList().get(0).getPayNo());
    }

    @Test
    void getTrade_notFound_throwNotFound() {
        when(payTradeMapper.selectById(765999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> paymentTradeService.getTrade(765999L));
    }
}
