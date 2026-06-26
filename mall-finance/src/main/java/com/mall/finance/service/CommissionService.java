package com.mall.finance.service;

import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.SplitFlowDTO;
import com.mall.api.finance.vo.CommissionDetailQueryVO;
import com.mall.api.finance.vo.SplitFlowQueryVO;
import com.mall.common.page.PageResult;

public interface CommissionService {

    PageResult<CommissionDetailDTO> pageCommissionDetails(CommissionDetailQueryVO query);

    CommissionDetailDTO getCommissionDetail(Long id);

    PageResult<SplitFlowDTO> pageSplitFlows(SplitFlowQueryVO query);

    SplitFlowDTO getSplitFlow(Long id);
}
