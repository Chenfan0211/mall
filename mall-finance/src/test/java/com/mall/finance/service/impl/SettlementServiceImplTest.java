package com.mall.finance.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.finance.dto.ReconciliationBatchDTO;
import com.mall.api.finance.dto.SupplierSettlementDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.finance.convert.FinanceConvert;
import com.mall.finance.entity.FinReconciliationBatch;
import com.mall.finance.entity.FinSupplierSettlement;
import com.mall.finance.mapper.FinReconciliationBatchMapper;
import com.mall.finance.mapper.FinSupplierSettlementMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SettlementServiceImplTest {

    @Mock
    private FinSupplierSettlementMapper supplierSettlementMapper;

    @Mock
    private FinReconciliationBatchMapper reconciliationBatchMapper;

    @Mock
    private FinanceConvert financeConvert;

    @InjectMocks
    private SettlementServiceImpl settlementService;

    @Test
    void pageSupplierSettlements_success() {
        final FinSupplierSettlement settlement = new FinSupplierSettlement();
        settlement.setId(790601L);
        settlement.setSettlementNo("SETTLE_TEST_WAIT");
        final SupplierSettlementDTO settlementDTO = new SupplierSettlementDTO();
        settlementDTO.setId(790601L);
        settlementDTO.setSettlementNo("SETTLE_TEST_WAIT");
        final Page<FinSupplierSettlement> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(settlement));
        when(supplierSettlementMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toSupplierSettlementDTO(settlement)).thenReturn(settlementDTO);

        final PageResult<SupplierSettlementDTO> result = settlementService.pageSupplierSettlements(null);

        assertEquals(1L, result.getTotal());
        assertEquals("SETTLE_TEST_WAIT", result.getList().get(0).getSettlementNo());
    }

    @Test
    void getSupplierSettlement_notFound_throwNotFound() {
        when(supplierSettlementMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> settlementService.getSupplierSettlement(790999L));
    }

    @Test
    void pageReconciliationBatches_success() {
        final FinReconciliationBatch batch = new FinReconciliationBatch();
        batch.setId(790701L);
        batch.setReconcileNo("RECON_TEST_WAIT");
        final ReconciliationBatchDTO batchDTO = new ReconciliationBatchDTO();
        batchDTO.setId(790701L);
        batchDTO.setReconcileNo("RECON_TEST_WAIT");
        final Page<FinReconciliationBatch> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(batch));
        when(reconciliationBatchMapper.selectPage(any(), any())).thenReturn(page);
        when(financeConvert.toReconciliationBatchDTO(batch)).thenReturn(batchDTO);

        final PageResult<ReconciliationBatchDTO> result = settlementService.pageReconciliationBatches(null);

        assertEquals(1L, result.getTotal());
        assertEquals("RECON_TEST_WAIT", result.getList().get(0).getReconcileNo());
    }

    @Test
    void getReconciliationBatch_notFound_throwNotFound() {
        when(reconciliationBatchMapper.selectById(790999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> settlementService.getReconciliationBatch(790999L));
    }
}
