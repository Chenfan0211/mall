package com.mall.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.aftersale.convert.AfterSaleConvert;
import com.mall.aftersale.entity.AfsReturnRecord;
import com.mall.aftersale.mapper.AfsReturnRecordMapper;
import com.mall.aftersale.service.AfterSaleReturnRecordService;
import com.mall.api.aftersale.dto.AfterSaleReturnRecordDTO;
import com.mall.api.aftersale.vo.AfterSaleReturnRecordQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class AfterSaleReturnRecordServiceImpl implements AfterSaleReturnRecordService {

    private final AfsReturnRecordMapper returnRecordMapper;
    private final AfterSaleConvert afterSaleConvert;

    public AfterSaleReturnRecordServiceImpl(
            final AfsReturnRecordMapper returnRecordMapper,
            final AfterSaleConvert afterSaleConvert) {
        this.returnRecordMapper = returnRecordMapper;
        this.afterSaleConvert = afterSaleConvert;
    }

    @Override
    public PageResult<AfterSaleReturnRecordDTO> pageReturnRecords(final AfterSaleReturnRecordQueryVO query) {
        final AfterSaleReturnRecordQueryVO safeQuery = query == null ? new AfterSaleReturnRecordQueryVO() : query;
        final LambdaQueryWrapper<AfsReturnRecord> wrapper = new LambdaQueryWrapper<AfsReturnRecord>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), AfsReturnRecord::getReturnNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getAfterSaleId() != null, AfsReturnRecord::getAfterSaleId,
                        safeQuery.getAfterSaleId())
                .eq(safeQuery.getOrderItemId() != null, AfsReturnRecord::getOrderItemId,
                        safeQuery.getOrderItemId())
                .eq(safeQuery.getSkuId() != null, AfsReturnRecord::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getStationId() != null, AfsReturnRecord::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getWarehouseId() != null, AfsReturnRecord::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getReturnStatus() != null, AfsReturnRecord::getReturnStatus,
                        safeQuery.getReturnStatus())
                .orderByDesc(AfsReturnRecord::getId);
        final Page<AfsReturnRecord> page = returnRecordMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(afterSaleConvert::toAfterSaleReturnRecordDTO)
                .toList());
    }

    @Override
    public AfterSaleReturnRecordDTO getReturnRecord(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "退货记录ID不能为空");
        }
        final AfsReturnRecord record = returnRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "退货记录不存在");
        }
        return afterSaleConvert.toAfterSaleReturnRecordDTO(record);
    }
}
