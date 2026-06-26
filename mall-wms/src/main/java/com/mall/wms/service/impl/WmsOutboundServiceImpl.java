package com.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.api.wms.dto.DriverSignRecordDTO;
import com.mall.api.wms.dto.LoadingItemDTO;
import com.mall.api.wms.dto.LoadingOrderDTO;
import com.mall.api.wms.dto.OutboundOrderDTO;
import com.mall.api.wms.dto.PickItemDTO;
import com.mall.api.wms.dto.PickTaskDTO;
import com.mall.api.wms.dto.ReturnHandoverDTO;
import com.mall.api.wms.dto.WaveBatchDTO;
import com.mall.api.wms.dto.WmsOperationLogDTO;
import com.mall.api.wms.vo.WmsOutboundQueryVO;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsDeliveryOrder;
import com.mall.wms.entity.WmsDeliveryStation;
import com.mall.wms.entity.WmsDriverSignRecord;
import com.mall.wms.entity.WmsLoadingItem;
import com.mall.wms.entity.WmsLoadingOrder;
import com.mall.wms.entity.WmsOperationLog;
import com.mall.wms.entity.WmsOutboundOrder;
import com.mall.wms.entity.WmsPickItem;
import com.mall.wms.entity.WmsPickTask;
import com.mall.wms.entity.WmsReturnHandover;
import com.mall.wms.entity.WmsWaveBatch;
import com.mall.wms.mapper.WmsDeliveryOrderMapper;
import com.mall.wms.mapper.WmsDeliveryStationMapper;
import com.mall.wms.mapper.WmsDriverSignRecordMapper;
import com.mall.wms.mapper.WmsLoadingItemMapper;
import com.mall.wms.mapper.WmsLoadingOrderMapper;
import com.mall.wms.mapper.WmsOperationLogMapper;
import com.mall.wms.mapper.WmsOutboundOrderMapper;
import com.mall.wms.mapper.WmsPickItemMapper;
import com.mall.wms.mapper.WmsPickTaskMapper;
import com.mall.wms.mapper.WmsReturnHandoverMapper;
import com.mall.wms.mapper.WmsWaveBatchMapper;
import com.mall.wms.service.WmsOutboundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class WmsOutboundServiceImpl extends WmsReadSupport implements WmsOutboundService {

    private final WmsWaveBatchMapper waveBatchMapper;
    private final WmsPickTaskMapper pickTaskMapper;
    private final WmsPickItemMapper pickItemMapper;
    private final WmsLoadingOrderMapper loadingOrderMapper;
    private final WmsLoadingItemMapper loadingItemMapper;
    private final WmsOutboundOrderMapper outboundOrderMapper;
    private final WmsDeliveryOrderMapper deliveryOrderMapper;
    private final WmsDeliveryStationMapper deliveryStationMapper;
    private final WmsDriverSignRecordMapper driverSignRecordMapper;
    private final WmsReturnHandoverMapper returnHandoverMapper;
    private final WmsOperationLogMapper operationLogMapper;
    private final WmsConvert wmsConvert;

    public WmsOutboundServiceImpl(
            final WmsWaveBatchMapper waveBatchMapper,
            final WmsPickTaskMapper pickTaskMapper,
            final WmsPickItemMapper pickItemMapper,
            final WmsLoadingOrderMapper loadingOrderMapper,
            final WmsLoadingItemMapper loadingItemMapper,
            final WmsOutboundOrderMapper outboundOrderMapper,
            final WmsDeliveryOrderMapper deliveryOrderMapper,
            final WmsDeliveryStationMapper deliveryStationMapper,
            final WmsDriverSignRecordMapper driverSignRecordMapper,
            final WmsReturnHandoverMapper returnHandoverMapper,
            final WmsOperationLogMapper operationLogMapper,
            final WmsConvert wmsConvert) {
        this.waveBatchMapper = waveBatchMapper;
        this.pickTaskMapper = pickTaskMapper;
        this.pickItemMapper = pickItemMapper;
        this.loadingOrderMapper = loadingOrderMapper;
        this.loadingItemMapper = loadingItemMapper;
        this.outboundOrderMapper = outboundOrderMapper;
        this.deliveryOrderMapper = deliveryOrderMapper;
        this.deliveryStationMapper = deliveryStationMapper;
        this.driverSignRecordMapper = driverSignRecordMapper;
        this.returnHandoverMapper = returnHandoverMapper;
        this.operationLogMapper = operationLogMapper;
        this.wmsConvert = wmsConvert;
    }

    @Override
    public PageResult<WaveBatchDTO> pageWaveBatches(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsWaveBatch> wrapper = new LambdaQueryWrapper<WmsWaveBatch>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsWaveBatch::getWaveNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getWarehouseId() != null, WmsWaveBatch::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getLineId() != null, WmsWaveBatch::getLineId, safeQuery.getLineId())
                .eq(safeQuery.getDeliveryDate() != null, WmsWaveBatch::getDeliveryDate,
                        safeQuery.getDeliveryDate())
                .eq(safeQuery.getStatus() != null, WmsWaveBatch::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsWaveBatch::getId);
        return toPage(safeQuery, waveBatchMapper, wrapper, wmsConvert::toWaveBatchDTO);
    }

    @Override
    public WaveBatchDTO getWaveBatch(final Long id) {
        return getById(id, waveBatchMapper, wmsConvert::toWaveBatchDTO, "WaveBatch");
    }

    @Override
    public PageResult<PickTaskDTO> pagePickTasks(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsPickTask> wrapper = new LambdaQueryWrapper<WmsPickTask>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsPickTask::getPickNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getWaveId() != null, WmsPickTask::getWaveId, safeQuery.getWaveId())
                .eq(safeQuery.getWarehouseId() != null, WmsPickTask::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getLineId() != null, WmsPickTask::getLineId, safeQuery.getLineId())
                .eq(safeQuery.getStatus() != null, WmsPickTask::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsPickTask::getId);
        return toPage(safeQuery, pickTaskMapper, wrapper, wmsConvert::toPickTaskDTO);
    }

    @Override
    public PickTaskDTO getPickTask(final Long id) {
        return getById(id, pickTaskMapper, wmsConvert::toPickTaskDTO, "PickTask");
    }

    @Override
    public PageResult<PickItemDTO> pagePickItems(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsPickItem> wrapper = new LambdaQueryWrapper<WmsPickItem>()
                .eq(safeQuery.getPickId() != null, WmsPickItem::getPickId, safeQuery.getPickId())
                .eq(safeQuery.getSkuId() != null, WmsPickItem::getSkuId, safeQuery.getSkuId())
                .orderByDesc(WmsPickItem::getId);
        return toPage(safeQuery, pickItemMapper, wrapper, wmsConvert::toPickItemDTO);
    }

    @Override
    public PickItemDTO getPickItem(final Long id) {
        return getById(id, pickItemMapper, wmsConvert::toPickItemDTO, "PickItem");
    }

    @Override
    public PageResult<LoadingOrderDTO> pageLoadingOrders(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsLoadingOrder> wrapper = new LambdaQueryWrapper<WmsLoadingOrder>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsLoadingOrder::getLoadingNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getWaveId() != null, WmsLoadingOrder::getWaveId, safeQuery.getWaveId())
                .eq(safeQuery.getWarehouseId() != null, WmsLoadingOrder::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getLineId() != null, WmsLoadingOrder::getLineId, safeQuery.getLineId())
                .eq(safeQuery.getDriverAccountId() != null, WmsLoadingOrder::getDriverAccountId,
                        safeQuery.getDriverAccountId())
                .eq(safeQuery.getDeliveryDate() != null, WmsLoadingOrder::getDeliveryDate,
                        safeQuery.getDeliveryDate())
                .eq(safeQuery.getStatus() != null, WmsLoadingOrder::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsLoadingOrder::getId);
        return toPage(safeQuery, loadingOrderMapper, wrapper, wmsConvert::toLoadingOrderDTO);
    }

    @Override
    public LoadingOrderDTO getLoadingOrder(final Long id) {
        return getById(id, loadingOrderMapper, wmsConvert::toLoadingOrderDTO, "LoadingOrder");
    }

    @Override
    public PageResult<LoadingItemDTO> pageLoadingItems(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsLoadingItem> wrapper = new LambdaQueryWrapper<WmsLoadingItem>()
                .eq(safeQuery.getLoadingId() != null, WmsLoadingItem::getLoadingId, safeQuery.getLoadingId())
                .eq(safeQuery.getStationId() != null, WmsLoadingItem::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getSkuId() != null, WmsLoadingItem::getSkuId, safeQuery.getSkuId())
                .orderByDesc(WmsLoadingItem::getId);
        return toPage(safeQuery, loadingItemMapper, wrapper, wmsConvert::toLoadingItemDTO);
    }

    @Override
    public LoadingItemDTO getLoadingItem(final Long id) {
        return getById(id, loadingItemMapper, wmsConvert::toLoadingItemDTO, "LoadingItem");
    }

    @Override
    public PageResult<OutboundOrderDTO> pageOutboundOrders(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsOutboundOrder> wrapper = new LambdaQueryWrapper<WmsOutboundOrder>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsOutboundOrder::getOutboundNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getLoadingId() != null, WmsOutboundOrder::getLoadingId, safeQuery.getLoadingId())
                .eq(safeQuery.getWarehouseId() != null, WmsOutboundOrder::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getLineId() != null, WmsOutboundOrder::getLineId, safeQuery.getLineId())
                .eq(safeQuery.getStatus() != null, WmsOutboundOrder::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsOutboundOrder::getId);
        return toPage(safeQuery, outboundOrderMapper, wrapper, wmsConvert::toOutboundOrderDTO);
    }

    @Override
    public OutboundOrderDTO getOutboundOrder(final Long id) {
        return getById(id, outboundOrderMapper, wmsConvert::toOutboundOrderDTO, "OutboundOrder");
    }

    @Override
    public PageResult<DeliveryOrderDTO> pageDeliveryOrders(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsDeliveryOrder> wrapper = new LambdaQueryWrapper<WmsDeliveryOrder>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsDeliveryOrder::getDeliveryNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getOutboundId() != null, WmsDeliveryOrder::getOutboundId, safeQuery.getOutboundId())
                .eq(safeQuery.getLoadingId() != null, WmsDeliveryOrder::getLoadingId, safeQuery.getLoadingId())
                .eq(safeQuery.getWarehouseId() != null, WmsDeliveryOrder::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getLineId() != null, WmsDeliveryOrder::getLineId, safeQuery.getLineId())
                .eq(safeQuery.getDriverAccountId() != null, WmsDeliveryOrder::getDriverAccountId,
                        safeQuery.getDriverAccountId())
                .eq(safeQuery.getDeliveryDate() != null, WmsDeliveryOrder::getDeliveryDate,
                        safeQuery.getDeliveryDate())
                .eq(safeQuery.getStatus() != null, WmsDeliveryOrder::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsDeliveryOrder::getId);
        return toPage(safeQuery, deliveryOrderMapper, wrapper, wmsConvert::toDeliveryOrderDTO);
    }

    @Override
    public DeliveryOrderDTO getDeliveryOrder(final Long id) {
        return getById(id, deliveryOrderMapper, wmsConvert::toDeliveryOrderDTO, "DeliveryOrder");
    }

    @Override
    public PageResult<DeliveryStationDTO> pageDeliveryStations(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsDeliveryStation> wrapper = new LambdaQueryWrapper<WmsDeliveryStation>()
                .eq(safeQuery.getDeliveryId() != null, WmsDeliveryStation::getDeliveryId,
                        safeQuery.getDeliveryId())
                .eq(safeQuery.getStationId() != null, WmsDeliveryStation::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getStatus() != null, WmsDeliveryStation::getStatus, safeQuery.getStatus())
                .orderByAsc(WmsDeliveryStation::getDeliverySort)
                .orderByDesc(WmsDeliveryStation::getId);
        return toPage(safeQuery, deliveryStationMapper, wrapper, wmsConvert::toDeliveryStationDTO);
    }

    @Override
    public DeliveryStationDTO getDeliveryStation(final Long id) {
        return getById(id, deliveryStationMapper, wmsConvert::toDeliveryStationDTO, "DeliveryStation");
    }

    @Override
    public PageResult<DriverSignRecordDTO> pageDriverSignRecords(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsDriverSignRecord> wrapper = new LambdaQueryWrapper<WmsDriverSignRecord>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsDriverSignRecord::getSignNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getDeliveryId() != null, WmsDriverSignRecord::getDeliveryId,
                        safeQuery.getDeliveryId())
                .eq(safeQuery.getStationId() != null, WmsDriverSignRecord::getStationId,
                        safeQuery.getStationId())
                .eq(safeQuery.getDriverAccountId() != null, WmsDriverSignRecord::getDriverAccountId,
                        safeQuery.getDriverAccountId())
                .eq(safeQuery.getSignType() != null, WmsDriverSignRecord::getSignType, safeQuery.getSignType())
                .orderByDesc(WmsDriverSignRecord::getId);
        return toPage(safeQuery, driverSignRecordMapper, wrapper, wmsConvert::toDriverSignRecordDTO);
    }

    @Override
    public DriverSignRecordDTO getDriverSignRecord(final Long id) {
        return getById(id, driverSignRecordMapper, wmsConvert::toDriverSignRecordDTO, "DriverSignRecord");
    }

    @Override
    public PageResult<ReturnHandoverDTO> pageReturnHandovers(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsReturnHandover> wrapper = new LambdaQueryWrapper<WmsReturnHandover>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsReturnHandover::getReturnNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getDeliveryId() != null, WmsReturnHandover::getDeliveryId,
                        safeQuery.getDeliveryId())
                .eq(safeQuery.getStationId() != null, WmsReturnHandover::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getSkuId() != null, WmsReturnHandover::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getWarehouseId() != null, WmsReturnHandover::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getDriverAccountId() != null, WmsReturnHandover::getDriverAccountId,
                        safeQuery.getDriverAccountId())
                .eq(safeQuery.getReasonType() != null, WmsReturnHandover::getReasonType,
                        safeQuery.getReasonType())
                .eq(safeQuery.getStatus() != null, WmsReturnHandover::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsReturnHandover::getId);
        return toPage(safeQuery, returnHandoverMapper, wrapper, wmsConvert::toReturnHandoverDTO);
    }

    @Override
    public ReturnHandoverDTO getReturnHandover(final Long id) {
        return getById(id, returnHandoverMapper, wmsConvert::toReturnHandoverDTO, "ReturnHandover");
    }

    @Override
    public PageResult<WmsOperationLogDTO> pageOperationLogs(final WmsOutboundQueryVO query) {
        final WmsOutboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsOperationLog> wrapper = new LambdaQueryWrapper<WmsOperationLog>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsOperationLog::getLogNo, safeQuery.getKeyword())
                        .or()
                        .like(WmsOperationLog::getBizNo, safeQuery.getKeyword()))
                .eq(safeQuery.getWarehouseId() != null, WmsOperationLog::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getAccountId() != null, WmsOperationLog::getAccountId, safeQuery.getAccountId())
                .eq(StringUtils.hasText(safeQuery.getModuleCode()), WmsOperationLog::getModuleCode,
                        safeQuery.getModuleCode())
                .eq(StringUtils.hasText(safeQuery.getActionCode()), WmsOperationLog::getActionCode,
                        safeQuery.getActionCode())
                .eq(StringUtils.hasText(safeQuery.getBizType()), WmsOperationLog::getBizType, safeQuery.getBizType())
                .eq(StringUtils.hasText(safeQuery.getBizNo()), WmsOperationLog::getBizNo, safeQuery.getBizNo())
                .eq(safeQuery.getResultStatus() != null, WmsOperationLog::getResultStatus,
                        safeQuery.getResultStatus())
                .orderByDesc(WmsOperationLog::getId);
        return toPage(safeQuery, operationLogMapper, wrapper, wmsConvert::toWmsOperationLogDTO);
    }

    @Override
    public WmsOperationLogDTO getOperationLog(final Long id) {
        return getById(id, operationLogMapper, wmsConvert::toWmsOperationLogDTO, "WmsOperationLog");
    }

    private WmsOutboundQueryVO safeQuery(final WmsOutboundQueryVO query) {
        return query == null ? new WmsOutboundQueryVO() : query;
    }
}
