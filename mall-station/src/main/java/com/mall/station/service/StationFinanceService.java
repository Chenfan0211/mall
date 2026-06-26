package com.mall.station.service;

import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.station.dto.StationMessageDTO;
import com.mall.api.station.vo.StationFinanceQueryVO;
import com.mall.api.station.vo.StationMessageQueryVO;
import com.mall.api.station.vo.StationWithdrawApplyVO;
import com.mall.common.page.PageResult;

public interface StationFinanceService {

    PageResult<FinanceAccountDTO> pageAccounts(StationFinanceQueryVO query);

    FinanceAccountDTO getAccount(Long id);

    PageResult<FinanceAccountFlowDTO> pageAccountFlows(StationFinanceQueryVO query);

    FinanceAccountFlowDTO getAccountFlow(Long id);

    PageResult<CommissionDetailDTO> pageCommissions(StationFinanceQueryVO query);

    CommissionDetailDTO getCommission(Long id);

    PageResult<WithdrawApplyDTO> pageWithdraws(StationFinanceQueryVO query);

    WithdrawApplyDTO getWithdraw(Long id);

    WithdrawApplyDTO applyWithdraw(StationWithdrawApplyVO request);

    WithdrawApplyDTO cancelWithdraw(Long id);

    PageResult<StationMessageDTO> pageMessages(StationMessageQueryVO query);

    StationMessageDTO getMessage(Long id);

    StationMessageDTO markMessageRead(Long id);
}
