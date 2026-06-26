package com.mall.operation.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.operation.dto.OperationDashboardSummaryDTO;
import com.mall.api.operation.dto.TodoDTO;
import com.mall.operation.convert.OperationConvert;
import com.mall.operation.entity.OpExceptionRecord;
import com.mall.operation.entity.OpTodo;
import com.mall.operation.mapper.OpExceptionRecordMapper;
import com.mall.operation.mapper.OpTodoMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationDashboardServiceImplTest {

    @Mock
    private OpTodoMapper todoMapper;

    @Mock
    private OpExceptionRecordMapper exceptionRecordMapper;

    @Mock
    private OperationConvert operationConvert;

    @InjectMocks
    private OperationDashboardServiceImpl operationDashboardService;

    @Test
    void getSummary_success() {
        final OpTodo todo = new OpTodo();
        todo.setId(791001L);
        final TodoDTO todoDTO = new TodoDTO();
        todoDTO.setId(791001L);
        final Page<OpTodo> todoPage = Page.of(1L, 5L);
        todoPage.setRecords(List.of(todo));
        final OpExceptionRecord record = new OpExceptionRecord();
        record.setId(791101L);
        final ExceptionRecordDTO recordDTO = new ExceptionRecordDTO();
        recordDTO.setId(791101L);
        final Page<OpExceptionRecord> exceptionPage = Page.of(1L, 5L);
        exceptionPage.setRecords(List.of(record));
        when(todoMapper.selectCount(any())).thenReturn(2L, 1L, 1L, 3L);
        when(exceptionRecordMapper.selectCount(any())).thenReturn(4L, 2L, 1L, 3L);
        when(todoMapper.selectPage(any(), any())).thenReturn(todoPage);
        when(exceptionRecordMapper.selectPage(any(), any())).thenReturn(exceptionPage);
        when(operationConvert.toTodoDTO(todo)).thenReturn(todoDTO);
        when(operationConvert.toExceptionRecordDTO(record)).thenReturn(recordDTO);

        final OperationDashboardSummaryDTO summary = operationDashboardService.getSummary();

        assertEquals(2L, summary.getPendingTodoCount());
        assertEquals(3L, summary.getHighPriorityTodoCount());
        assertEquals(4L, summary.getPendingExceptionCount());
        assertEquals(3L, summary.getHighPriorityExceptionCount());
        assertEquals(791001L, summary.getUrgentTodos().get(0).getId());
        assertEquals(791101L, summary.getWarningExceptions().get(0).getId());
    }
}
