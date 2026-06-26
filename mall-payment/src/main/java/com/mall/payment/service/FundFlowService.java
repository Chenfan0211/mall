package com.mall.payment.service;

import com.mall.api.payment.dto.FundFlowDTO;
import com.mall.api.payment.vo.FundFlowQueryVO;
import com.mall.common.page.PageResult;

public interface FundFlowService {

    PageResult<FundFlowDTO> pageFlows(FundFlowQueryVO query);

    FundFlowDTO getFlow(Long id);
}
