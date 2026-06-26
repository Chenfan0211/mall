package com.mall.trade.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.trade.dto.StockoutRecordDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdStockoutRecord;
import com.mall.trade.mapper.OrdStockoutRecordMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockoutRecordServiceImplTest {

    @Mock
    private OrdStockoutRecordMapper stockoutRecordMapper;

    @Mock
    private TradeConvert tradeConvert;

    @InjectMocks
    private StockoutRecordServiceImpl stockoutRecordService;

    @Test
    void pageStockouts_success() {
        final OrdStockoutRecord record = new OrdStockoutRecord();
        record.setId(784301L);
        record.setStockoutNo("STOCKOUT_TEST_WAIT");
        final StockoutRecordDTO recordDTO = new StockoutRecordDTO();
        recordDTO.setId(784301L);
        recordDTO.setStockoutNo("STOCKOUT_TEST_WAIT");
        final Page<OrdStockoutRecord> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(record));
        when(stockoutRecordMapper.selectPage(any(), any())).thenReturn(page);
        when(tradeConvert.toStockoutRecordDTO(record)).thenReturn(recordDTO);

        final PageResult<StockoutRecordDTO> result = stockoutRecordService.pageStockouts(null);

        assertEquals(1L, result.getTotal());
        assertEquals("STOCKOUT_TEST_WAIT", result.getList().get(0).getStockoutNo());
    }

    @Test
    void getStockout_notFound_throwNotFound() {
        when(stockoutRecordMapper.selectById(784999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> stockoutRecordService.getStockout(784999L));
    }
}
