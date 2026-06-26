package com.mall.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.trade.dto.StockoutRecordDTO;
import com.mall.api.trade.vo.StockoutRecordQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdStockoutRecord;
import com.mall.trade.mapper.OrdStockoutRecordMapper;
import com.mall.trade.service.StockoutRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockoutRecordServiceImpl implements StockoutRecordService {

    private final OrdStockoutRecordMapper stockoutRecordMapper;
    private final TradeConvert tradeConvert;

    public StockoutRecordServiceImpl(
            final OrdStockoutRecordMapper stockoutRecordMapper,
            final TradeConvert tradeConvert) {
        this.stockoutRecordMapper = stockoutRecordMapper;
        this.tradeConvert = tradeConvert;
    }

    @Override
    public PageResult<StockoutRecordDTO> pageStockouts(final StockoutRecordQueryVO query) {
        final StockoutRecordQueryVO safeQuery = query == null ? new StockoutRecordQueryVO() : query;
        final LambdaQueryWrapper<OrdStockoutRecord> wrapper = new LambdaQueryWrapper<OrdStockoutRecord>()
                .eq(safeQuery.getPeriodId() != null, OrdStockoutRecord::getPeriodId, safeQuery.getPeriodId())
                .eq(safeQuery.getOrderId() != null, OrdStockoutRecord::getOrderId, safeQuery.getOrderId())
                .eq(safeQuery.getOrderItemId() != null, OrdStockoutRecord::getOrderItemId,
                        safeQuery.getOrderItemId())
                .eq(safeQuery.getSkuId() != null, OrdStockoutRecord::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getWarehouseId() != null, OrdStockoutRecord::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getStationId() != null, OrdStockoutRecord::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getStatus() != null, OrdStockoutRecord::getStatus, safeQuery.getStatus())
                .orderByDesc(OrdStockoutRecord::getId);
        final Page<OrdStockoutRecord> page = stockoutRecordMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(tradeConvert::toStockoutRecordDTO)
                .toList());
    }

    @Override
    public StockoutRecordDTO getStockout(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "缺货记录ID不能为空");
        }
        final OrdStockoutRecord stockoutRecord = stockoutRecordMapper.selectById(id);
        if (stockoutRecord == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "缺货记录不存在");
        }
        return tradeConvert.toStockoutRecordDTO(stockoutRecord);
    }
}
