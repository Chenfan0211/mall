package com.mall.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.aftersale.convert.AfterSaleConvert;
import com.mall.aftersale.entity.AfsAfterSale;
import com.mall.aftersale.entity.AfsAuditLog;
import com.mall.aftersale.mapper.AfsAfterSaleMapper;
import com.mall.aftersale.mapper.AfsAuditLogMapper;
import com.mall.aftersale.service.AfterSaleService;
import com.mall.api.aftersale.dto.AfterSaleAuditLogDTO;
import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.vo.AfterSaleQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class AfterSaleServiceImpl implements AfterSaleService {

    private final AfsAfterSaleMapper afterSaleMapper;
    private final AfsAuditLogMapper auditLogMapper;
    private final AfterSaleConvert afterSaleConvert;

    public AfterSaleServiceImpl(
            final AfsAfterSaleMapper afterSaleMapper,
            final AfsAuditLogMapper auditLogMapper,
            final AfterSaleConvert afterSaleConvert) {
        this.afterSaleMapper = afterSaleMapper;
        this.auditLogMapper = auditLogMapper;
        this.afterSaleConvert = afterSaleConvert;
    }

    @Override
    public PageResult<AfterSaleDTO> pageAfterSales(final AfterSaleQueryVO query) {
        final AfterSaleQueryVO safeQuery = query == null ? new AfterSaleQueryVO() : query;
        final LambdaQueryWrapper<AfsAfterSale> wrapper = new LambdaQueryWrapper<AfsAfterSale>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(AfsAfterSale::getAfterSaleNo, safeQuery.getKeyword())
                        .or()
                        .like(AfsAfterSale::getOrderNo, safeQuery.getKeyword()))
                .eq(safeQuery.getOrderId() != null, AfsAfterSale::getOrderId, safeQuery.getOrderId())
                .eq(StringUtils.hasText(safeQuery.getOrderNo()), AfsAfterSale::getOrderNo, safeQuery.getOrderNo())
                .eq(safeQuery.getUserId() != null, AfsAfterSale::getUserId, safeQuery.getUserId())
                .eq(safeQuery.getCityId() != null, AfsAfterSale::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStationId() != null, AfsAfterSale::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getAfterSaleType() != null, AfsAfterSale::getAfterSaleType,
                        safeQuery.getAfterSaleType())
                .eq(safeQuery.getStatus() != null, AfsAfterSale::getStatus, safeQuery.getStatus())
                .eq(safeQuery.getResponsibilityType() != null, AfsAfterSale::getResponsibilityType,
                        safeQuery.getResponsibilityType())
                .orderByDesc(AfsAfterSale::getId);
        final Page<AfsAfterSale> page = afterSaleMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(afterSaleConvert::toAfterSaleDTO)
                .toList());
    }

    @Override
    public AfterSaleDTO getAfterSale(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "售后单ID不能为空");
        }
        final AfsAfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "售后单不存在");
        }
        return afterSaleConvert.toAfterSaleDTO(afterSale);
    }

    @Override
    public List<AfterSaleAuditLogDTO> listAuditLogs(final Long afterSaleId) {
        if (afterSaleId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "售后单ID不能为空");
        }
        final LambdaQueryWrapper<AfsAuditLog> wrapper = new LambdaQueryWrapper<AfsAuditLog>()
                .eq(AfsAuditLog::getAfterSaleId, afterSaleId)
                .orderByAsc(AfsAuditLog::getCreateTime)
                .orderByAsc(AfsAuditLog::getId);
        return auditLogMapper.selectList(wrapper).stream()
                .map(afterSaleConvert::toAfterSaleAuditLogDTO)
                .toList();
    }
}
