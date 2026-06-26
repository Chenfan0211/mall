package com.mall.operation.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.operation.vo.ExceptionRecordQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.operation.convert.OperationConvert;
import com.mall.operation.entity.OpExceptionRecord;
import com.mall.operation.mapper.OpExceptionRecordMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExceptionRecordServiceImplTest {

    @Mock
    private OpExceptionRecordMapper exceptionRecordMapper;

    @Mock
    private OperationConvert operationConvert;

    @InjectMocks
    private ExceptionRecordServiceImpl exceptionRecordService;

    @Test
    void pageExceptionRecords_success() {
        final ExceptionRecordQueryVO query = new ExceptionRecordQueryVO();
        query.setStatus(10L);
        final OpExceptionRecord record = new OpExceptionRecord();
        record.setId(791101L);
        record.setTitle("拣货差异待处理");
        final Page<OpExceptionRecord> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(record));
        final ExceptionRecordDTO dto = new ExceptionRecordDTO();
        dto.setId(791101L);
        dto.setTitle("拣货差异待处理");
        when(exceptionRecordMapper.selectPage(any(), any())).thenReturn(page);
        when(operationConvert.toExceptionRecordDTO(record)).thenReturn(dto);

        final PageResult<ExceptionRecordDTO> result = exceptionRecordService.pageExceptionRecords(query);

        assertEquals(1L, result.getTotal());
        assertEquals("拣货差异待处理", result.getList().get(0).getTitle());
    }

    @Test
    void getExceptionRecord_notFound_throwNotFound() {
        when(exceptionRecordMapper.selectById(791999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> exceptionRecordService.getExceptionRecord(791999L));
    }
}
