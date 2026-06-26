package com.mall.finance.service;

import com.mall.api.finance.dto.ReconciliationBatchDTO;
import com.mall.api.finance.dto.SupplierSettlementDTO;
import com.mall.api.finance.vo.ReconciliationBatchQueryVO;
import com.mall.api.finance.vo.SupplierSettlementQueryVO;
import com.mall.common.page.PageResult;

public interface SettlementService {

    PageResult<SupplierSettlementDTO> pageSupplierSettlements(SupplierSettlementQueryVO query);

    SupplierSettlementDTO getSupplierSettlement(Long id);

    PageResult<ReconciliationBatchDTO> pageReconciliationBatches(ReconciliationBatchQueryVO query);

    ReconciliationBatchDTO getReconciliationBatch(Long id);
}
