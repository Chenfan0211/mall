package com.mall.finance.convert;

import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.DepositRecordDTO;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.dto.ReconciliationBatchDTO;
import com.mall.api.finance.dto.SplitFlowDTO;
import com.mall.api.finance.dto.SupplierSettlementDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.finance.entity.FinAccount;
import com.mall.finance.entity.FinAccountFlow;
import com.mall.finance.entity.FinCommissionDetail;
import com.mall.finance.entity.FinDepositRecord;
import com.mall.finance.entity.FinReconciliationBatch;
import com.mall.finance.entity.FinSplitFlow;
import com.mall.finance.entity.FinSupplierSettlement;
import com.mall.finance.entity.FinWithdrawApply;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FinanceConvert {

    FinanceAccountDTO toFinanceAccountDTO(FinAccount entity);

    FinanceAccountFlowDTO toFinanceAccountFlowDTO(FinAccountFlow entity);

    CommissionDetailDTO toCommissionDetailDTO(FinCommissionDetail entity);

    SplitFlowDTO toSplitFlowDTO(FinSplitFlow entity);

    WithdrawApplyDTO toWithdrawApplyDTO(FinWithdrawApply entity);

    DepositRecordDTO toDepositRecordDTO(FinDepositRecord entity);

    SupplierSettlementDTO toSupplierSettlementDTO(FinSupplierSettlement entity);

    ReconciliationBatchDTO toReconciliationBatchDTO(FinReconciliationBatch entity);
}
