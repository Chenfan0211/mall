package com.mall.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.operation.dto.TodoDTO;
import com.mall.api.operation.vo.TodoQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.operation.convert.OperationConvert;
import com.mall.operation.entity.OpTodo;
import com.mall.operation.mapper.OpTodoMapper;
import com.mall.operation.service.TodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class TodoServiceImpl implements TodoService {

    private final OpTodoMapper todoMapper;
    private final OperationConvert operationConvert;

    public TodoServiceImpl(final OpTodoMapper todoMapper, final OperationConvert operationConvert) {
        this.todoMapper = todoMapper;
        this.operationConvert = operationConvert;
    }

    @Override
    public PageResult<TodoDTO> pageTodos(final TodoQueryVO query) {
        final TodoQueryVO safeQuery = query == null ? new TodoQueryVO() : query;
        final LambdaQueryWrapper<OpTodo> wrapper = new LambdaQueryWrapper<OpTodo>()
                .eq(safeQuery.getTodoType() != null, OpTodo::getTodoType, safeQuery.getTodoType())
                .eq(safeQuery.getPriority() != null, OpTodo::getPriority, safeQuery.getPriority())
                .eq(safeQuery.getStatus() != null, OpTodo::getStatus, safeQuery.getStatus())
                .eq(StringUtils.hasText(safeQuery.getSourceBizType()), OpTodo::getSourceBizType,
                        safeQuery.getSourceBizType())
                .eq(safeQuery.getProcessAccountId() != null, OpTodo::getProcessAccountId,
                        safeQuery.getProcessAccountId())
                .eq(safeQuery.getCityId() != null, OpTodo::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getSupplierId() != null, OpTodo::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getWarehouseId() != null, OpTodo::getWarehouseId, safeQuery.getWarehouseId())
                .orderByAsc(OpTodo::getPriority)
                .orderByAsc(OpTodo::getDueTime)
                .orderByDesc(OpTodo::getId);
        final Page<OpTodo> page = todoMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream().map(operationConvert::toTodoDTO).toList());
    }

    @Override
    public TodoDTO getTodo(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "待办ID不能为空");
        }
        final OpTodo todo = todoMapper.selectById(id);
        if (todo == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "待办不存在");
        }
        return operationConvert.toTodoDTO(todo);
    }
}
