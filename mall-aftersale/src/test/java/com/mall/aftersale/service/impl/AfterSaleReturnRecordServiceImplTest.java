package com.mall.aftersale.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.aftersale.convert.AfterSaleConvert;
import com.mall.aftersale.entity.AfsReturnRecord;
import com.mall.aftersale.mapper.AfsReturnRecordMapper;
import com.mall.api.aftersale.dto.AfterSaleReturnRecordDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AfterSaleReturnRecordServiceImplTest {

    @Mock
    private AfsReturnRecordMapper returnRecordMapper;

    @Mock
    private AfterSaleConvert afterSaleConvert;

    @InjectMocks
    private AfterSaleReturnRecordServiceImpl returnRecordService;

    @Test
    void pageReturnRecords_success() {
        final AfsReturnRecord record = new AfsReturnRecord();
        record.setId(767301L);
        record.setReturnNo("AFS_RET_TEST_WAIT");
        final AfterSaleReturnRecordDTO recordDTO = new AfterSaleReturnRecordDTO();
        recordDTO.setId(767301L);
        recordDTO.setReturnNo("AFS_RET_TEST_WAIT");
        final Page<AfsReturnRecord> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(record));
        when(returnRecordMapper.selectPage(any(), any())).thenReturn(page);
        when(afterSaleConvert.toAfterSaleReturnRecordDTO(record)).thenReturn(recordDTO);

        final PageResult<AfterSaleReturnRecordDTO> result = returnRecordService.pageReturnRecords(null);

        assertEquals(1L, result.getTotal());
        assertEquals("AFS_RET_TEST_WAIT", result.getList().get(0).getReturnNo());
    }

    @Test
    void getReturnRecord_notFound_throwNotFound() {
        when(returnRecordMapper.selectById(767999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> returnRecordService.getReturnRecord(767999L));
    }
}
