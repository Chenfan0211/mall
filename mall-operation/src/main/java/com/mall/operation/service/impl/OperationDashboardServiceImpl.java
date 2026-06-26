package com.mall.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.operation.dto.OperationDashboardSummaryDTO;
import com.mall.operation.convert.OperationConvert;
import com.mall.operation.entity.OpExceptionRecord;
import com.mall.operation.entity.OpTodo;
import com.mall.operation.mapper.OpExceptionRecordMapper;
import com.mall.operation.mapper.OpTodoMapper;
import com.mall.operation.service.OperationDashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OperationDashboardServiceImpl implements OperationDashboardService {

    private static final Long WORK_STATUS_PENDING = 10L;
    private static final Long WORK_STATUS_PROCESSING = 20L;
    private static final Long WORK_STATUS_TIMEOUT = 60L;
    private static final Long EXCEPTION_STATUS_PENDING = 10L;
    private static final Long EXCEPTION_STATUS_PROCESSING = 20L;
    private static final Long EXCEPTION_STATUS_CLOSED = 40L;
    private static final Long PRIORITY_HIGH = 1L;
    private static final Long PREVIEW_LIMIT = 5L;

    private final OpTodoMapper todoMapper;
    private final OpExceptionRecordMapper exceptionRecordMapper;
    private final OperationConvert operationConvert;

    public OperationDashboardServiceImpl(
            final OpTodoMapper todoMapper,
            final OpExceptionRecordMapper exceptionRecordMapper,
            final OperationConvert operationConvert) {
        this.todoMapper = todoMapper;
        this.exceptionRecordMapper = exceptionRecordMapper;
        this.operationConvert = operationConvert;
    }

    @Override
    public OperationDashboardSummaryDTO getSummary() {
        final OperationDashboardSummaryDTO summary = new OperationDashboardSummaryDTO();
        summary.setPendingTodoCount(countTodoByStatus(WORK_STATUS_PENDING));
        summary.setProcessingTodoCount(countTodoByStatus(WORK_STATUS_PROCESSING));
        summary.setTimeoutTodoCount(countTodoByStatus(WORK_STATUS_TIMEOUT));
        summary.setHighPriorityTodoCount(todoMapper.selectCount(new LambdaQueryWrapper<OpTodo>()
                .eq(OpTodo::getPriority, PRIORITY_HIGH)
                .in(OpTodo::getStatus, WORK_STATUS_PENDING, WORK_STATUS_PROCESSING, WORK_STATUS_TIMEOUT)));
        summary.setPendingExceptionCount(countExceptionByStatus(EXCEPTION_STATUS_PENDING));
        summary.setProcessingExceptionCount(countExceptionByStatus(EXCEPTION_STATUS_PROCESSING));
        summary.setClosedExceptionCount(countExceptionByStatus(EXCEPTION_STATUS_CLOSED));
        summary.setHighPriorityExceptionCount(exceptionRecordMapper.selectCount(
                new LambdaQueryWrapper<OpExceptionRecord>()
                        .eq(OpExceptionRecord::getPriority, PRIORITY_HIGH)
                        .in(OpExceptionRecord::getStatus, EXCEPTION_STATUS_PENDING, EXCEPTION_STATUS_PROCESSING)));
        summary.setUrgentTodos(todoMapper.selectPage(Page.of(1L, PREVIEW_LIMIT), new LambdaQueryWrapper<OpTodo>()
                        .in(OpTodo::getStatus, WORK_STATUS_PENDING, WORK_STATUS_PROCESSING, WORK_STATUS_TIMEOUT)
                        .orderByAsc(OpTodo::getPriority)
                        .orderByAsc(OpTodo::getDueTime)
                        .orderByDesc(OpTodo::getId))
                .getRecords().stream().map(operationConvert::toTodoDTO).toList());
        summary.setWarningExceptions(exceptionRecordMapper.selectPage(
                        Page.of(1L, PREVIEW_LIMIT),
                        new LambdaQueryWrapper<OpExceptionRecord>()
                                .in(OpExceptionRecord::getStatus, EXCEPTION_STATUS_PENDING, EXCEPTION_STATUS_PROCESSING)
                                .orderByAsc(OpExceptionRecord::getPriority)
                                .orderByDesc(OpExceptionRecord::getId))
                .getRecords().stream().map(operationConvert::toExceptionRecordDTO).toList());
        return summary;
    }

    private Long countTodoByStatus(final Long status) {
        return todoMapper.selectCount(new LambdaQueryWrapper<OpTodo>().eq(OpTodo::getStatus, status));
    }

    private Long countExceptionByStatus(final Long status) {
        return exceptionRecordMapper.selectCount(
                new LambdaQueryWrapper<OpExceptionRecord>().eq(OpExceptionRecord::getStatus, status));
    }
}
