package com.mall.payment.service;

import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.payment.vo.PaymentTradeQueryVO;
import com.mall.common.page.PageResult;

public interface PaymentTradeService {

    PageResult<PaymentTradeDTO> pageTrades(PaymentTradeQueryVO query);

    PaymentTradeDTO getTrade(Long id);
}
