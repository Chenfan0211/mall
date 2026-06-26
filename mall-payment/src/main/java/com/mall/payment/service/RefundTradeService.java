package com.mall.payment.service;

import com.mall.api.payment.dto.RefundTradeDTO;
import com.mall.api.payment.vo.RefundTradeQueryVO;
import com.mall.common.page.PageResult;

public interface RefundTradeService {

    PageResult<RefundTradeDTO> pageRefunds(RefundTradeQueryVO query);

    RefundTradeDTO getRefund(Long id);
}
