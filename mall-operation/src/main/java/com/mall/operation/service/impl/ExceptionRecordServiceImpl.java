package com.mall.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.operation.vo.ExceptionRecordQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.operation.convert.OperationConvert;
import com.mall.operation.entity.OpExceptionRecord;
import com.mall.operation.mapper.OpExceptionRecordMapper;
import com.mall.operation.service.ExceptionRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class ExceptionRecordServiceImpl implements ExceptionRecordService {

    private final OpExceptionRecordMapper exceptionRecordMapper;
    private final OperationConvert operationConvert;

    public ExceptionRecordServiceImpl(
            final OpExceptionRecordMapper exceptionRecordMapper,
            final OperationConvert operationConvert) {
        this.exceptionRecordMapper = exceptionRecordMapper;
        this.operationConvert = operationConvert;
    }

    @Override
    public PageResult<ExceptionRecordDTO> pageExceptionRecords(final ExceptionRecordQueryVO query) {
        final ExceptionRecordQueryVO safeQuery = query == null ? new ExceptionRecordQueryVO() : query;
        final LambdaQueryWrapper<OpExceptionRecord> wrapper = new LambdaQueryWrapper<OpExceptionRecord>()
                .eq(safeQuery.getExceptionType() != null, OpExceptionRecord::getExceptionType,
                        safeQuery.getExceptionType())
                .eq(safeQuery.getPriority() != null, OpExceptionRecord::getPriority, safeQuery.getPriority())
                .eq(safeQuery.getStatus() != null, OpExceptionRecord::getStatus, safeQuery.getStatus())
                .eq(StringUtils.hasText(safeQuery.getSourceModule()), OpExceptionRecord::getSourceModule,
                        safeQuery.getSourceModule())
                .eq(StringUtils.hasText(safeQuery.getSourceBizType()), OpExceptionRecord::getSourceBizType,
                        safeQuery.getSourceBizType())
                .eq(safeQuery.getCityId() != null, OpExceptionRecord::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getSupplierId() != null, OpExceptionRecord::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getStationId() != null, OpExceptionRecord::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getWarehouseId() != null, OpExceptionRecord::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getOwnerAccountId() != null, OpExceptionRecord::getOwnerAccountId,
                        safeQuery.getOwnerAccountId())
                .orderByAsc(OpExceptionRecord::getPriority)
                .orderByAsc(OpExceptionRecord::getStatus)
                .orderByDesc(OpExceptionRecord::getId);
        final Page<OpExceptionRecord> page = exceptionRecordMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(
                page.getTotal(),
                page.getRecords().stream().map(operationConvert::toExceptionRecordDTO).toList());
    }

    @Override
    public ExceptionRecordDTO getExceptionRecord(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "异常ID不能为空");
        }
        final OpExceptionRecord record = exceptionRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "异常记录不存在");
        }
        return operationConvert.toExceptionRecordDTO(record);
    }
}
