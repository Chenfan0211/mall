package com.mall.finance.service;

import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.vo.FinanceAccountFlowQueryVO;
import com.mall.api.finance.vo.FinanceAccountQueryVO;
import com.mall.common.page.PageResult;

public interface FinanceAccountService {

    PageResult<FinanceAccountDTO> pageAccounts(FinanceAccountQueryVO query);

    FinanceAccountDTO getAccount(Long id);

    PageResult<FinanceAccountFlowDTO> pageAccountFlows(FinanceAccountFlowQueryVO query);

    FinanceAccountFlowDTO getAccountFlow(Long id);
}
