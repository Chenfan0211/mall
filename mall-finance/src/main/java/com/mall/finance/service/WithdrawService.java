package com.mall.finance.service;

import com.mall.api.finance.dto.DepositRecordDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.finance.vo.DepositRecordQueryVO;
import com.mall.api.finance.vo.WithdrawApplyQueryVO;
import com.mall.common.page.PageResult;

public interface WithdrawService {

    PageResult<WithdrawApplyDTO> pageWithdrawApplies(WithdrawApplyQueryVO query);

    WithdrawApplyDTO getWithdrawApply(Long id);

    PageResult<DepositRecordDTO> pageDepositRecords(DepositRecordQueryVO query);

    DepositRecordDTO getDepositRecord(Long id);
}
