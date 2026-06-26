package com.mall.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.sale.dto.PeriodSnapshotDTO;
import com.mall.api.sale.dto.PublishPeriodDTO;
import com.mall.api.sale.vo.PublishPeriodQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.sale.convert.SaleConvert;
import com.mall.sale.entity.SalePeriodSnapshot;
import com.mall.sale.entity.SalePublishPeriod;
import com.mall.sale.mapper.SalePeriodSnapshotMapper;
import com.mall.sale.mapper.SalePublishPeriodMapper;
import com.mall.sale.service.PublishPeriodService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class PublishPeriodServiceImpl implements PublishPeriodService {

    private final SalePublishPeriodMapper publishPeriodMapper;
    private final SalePeriodSnapshotMapper periodSnapshotMapper;
    private final SaleConvert saleConvert;

    public PublishPeriodServiceImpl(
            final SalePublishPeriodMapper publishPeriodMapper,
            final SalePeriodSnapshotMapper periodSnapshotMapper,
            final SaleConvert saleConvert) {
        this.publishPeriodMapper = publishPeriodMapper;
        this.periodSnapshotMapper = periodSnapshotMapper;
        this.saleConvert = saleConvert;
    }

    @Override
    public PageResult<PublishPeriodDTO> pagePeriods(final PublishPeriodQueryVO query) {
        final PublishPeriodQueryVO safeQuery = query == null ? new PublishPeriodQueryVO() : query;
        final LambdaQueryWrapper<SalePublishPeriod> wrapper = new LambdaQueryWrapper<SalePublishPeriod>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(SalePublishPeriod::getPeriodName, safeQuery.getKeyword())
                        .or()
                        .like(SalePublishPeriod::getPeriodNo, safeQuery.getKeyword()))
                .eq(safeQuery.getRegionId() != null, SalePublishPeriod::getRegionId, safeQuery.getRegionId())
                .eq(safeQuery.getCityId() != null, SalePublishPeriod::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getWarehouseId() != null, SalePublishPeriod::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getStockMode() != null, SalePublishPeriod::getStockMode, safeQuery.getStockMode())
                .eq(safeQuery.getStatus() != null, SalePublishPeriod::getStatus, safeQuery.getStatus())
                .orderByDesc(SalePublishPeriod::getId);
        final Page<SalePublishPeriod> page = publishPeriodMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(saleConvert::toPublishPeriodDTO)
                .toList());
    }

    @Override
    public PublishPeriodDTO getPeriod(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "团期ID不能为空");
        }
        final SalePublishPeriod period = publishPeriodMapper.selectById(id);
        if (period == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "团期不存在");
        }
        return saleConvert.toPublishPeriodDTO(period);
    }

    @Override
    public List<PeriodSnapshotDTO> listSnapshots(final Long periodId) {
        if (periodId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "团期ID不能为空");
        }
        final LambdaQueryWrapper<SalePeriodSnapshot> wrapper = new LambdaQueryWrapper<SalePeriodSnapshot>()
                .eq(SalePeriodSnapshot::getPeriodId, periodId)
                .orderByAsc(SalePeriodSnapshot::getSnapshotNode)
                .orderByAsc(SalePeriodSnapshot::getId);
        return periodSnapshotMapper.selectList(wrapper).stream().map(saleConvert::toPeriodSnapshotDTO).toList();
    }
}
