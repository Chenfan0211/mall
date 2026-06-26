package com.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.wms.dto.InboundItemDTO;
import com.mall.api.wms.dto.InboundOrderDTO;
import com.mall.api.wms.dto.PutawayTaskDTO;
import com.mall.api.wms.dto.QualityFileDTO;
import com.mall.api.wms.dto.ReceiveRecordDTO;
import com.mall.api.wms.vo.WmsInboundQueryVO;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsInboundItem;
import com.mall.wms.entity.WmsInboundOrder;
import com.mall.wms.entity.WmsPutawayTask;
import com.mall.wms.entity.WmsQualityFile;
import com.mall.wms.entity.WmsReceiveRecord;
import com.mall.wms.mapper.WmsInboundItemMapper;
import com.mall.wms.mapper.WmsInboundOrderMapper;
import com.mall.wms.mapper.WmsPutawayTaskMapper;
import com.mall.wms.mapper.WmsQualityFileMapper;
import com.mall.wms.mapper.WmsReceiveRecordMapper;
import com.mall.wms.service.WmsInboundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class WmsInboundServiceImpl extends WmsReadSupport implements WmsInboundService {

    private final WmsInboundOrderMapper inboundOrderMapper;
    private final WmsInboundItemMapper inboundItemMapper;
    private final WmsReceiveRecordMapper receiveRecordMapper;
    private final WmsQualityFileMapper qualityFileMapper;
    private final WmsPutawayTaskMapper putawayTaskMapper;
    private final WmsConvert wmsConvert;

    public WmsInboundServiceImpl(
            final WmsInboundOrderMapper inboundOrderMapper,
            final WmsInboundItemMapper inboundItemMapper,
            final WmsReceiveRecordMapper receiveRecordMapper,
            final WmsQualityFileMapper qualityFileMapper,
            final WmsPutawayTaskMapper putawayTaskMapper,
            final WmsConvert wmsConvert) {
        this.inboundOrderMapper = inboundOrderMapper;
        this.inboundItemMapper = inboundItemMapper;
        this.receiveRecordMapper = receiveRecordMapper;
        this.qualityFileMapper = qualityFileMapper;
        this.putawayTaskMapper = putawayTaskMapper;
        this.wmsConvert = wmsConvert;
    }

    @Override
    public PageResult<InboundOrderDTO> pageInboundOrders(final WmsInboundQueryVO query) {
        final WmsInboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsInboundOrder> wrapper = new LambdaQueryWrapper<WmsInboundOrder>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsInboundOrder::getInboundNo, safeQuery.getKeyword())
                        .or()
                        .like(WmsInboundOrder::getSourceNo, safeQuery.getKeyword()))
                .eq(safeQuery.getSourceType() != null, WmsInboundOrder::getSourceType, safeQuery.getSourceType())
                .eq(safeQuery.getSupplierId() != null, WmsInboundOrder::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getWarehouseId() != null, WmsInboundOrder::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getStatus() != null, WmsInboundOrder::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsInboundOrder::getId);
        return toPage(safeQuery, inboundOrderMapper, wrapper, wmsConvert::toInboundOrderDTO);
    }

    @Override
    public InboundOrderDTO getInboundOrder(final Long id) {
        return getById(id, inboundOrderMapper, wmsConvert::toInboundOrderDTO, "InboundOrder");
    }

    @Override
    public PageResult<InboundItemDTO> pageInboundItems(final WmsInboundQueryVO query) {
        final WmsInboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsInboundItem> wrapper = new LambdaQueryWrapper<WmsInboundItem>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsInboundItem::getBatchNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getInboundId() != null, WmsInboundItem::getInboundId, safeQuery.getInboundId())
                .eq(safeQuery.getSkuId() != null, WmsInboundItem::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getSupplierId() != null, WmsInboundItem::getSupplierId, safeQuery.getSupplierId())
                .orderByDesc(WmsInboundItem::getId);
        return toPage(safeQuery, inboundItemMapper, wrapper, wmsConvert::toInboundItemDTO);
    }

    @Override
    public InboundItemDTO getInboundItem(final Long id) {
        return getById(id, inboundItemMapper, wmsConvert::toInboundItemDTO, "InboundItem");
    }

    @Override
    public PageResult<ReceiveRecordDTO> pageReceiveRecords(final WmsInboundQueryVO query) {
        final WmsInboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsReceiveRecord> wrapper = new LambdaQueryWrapper<WmsReceiveRecord>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsReceiveRecord::getReceiveNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getInboundId() != null, WmsReceiveRecord::getInboundId, safeQuery.getInboundId())
                .eq(safeQuery.getInboundItemId() != null, WmsReceiveRecord::getInboundItemId,
                        safeQuery.getInboundItemId())
                .eq(safeQuery.getSkuId() != null, WmsReceiveRecord::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getQualityStatus() != null, WmsReceiveRecord::getQualityStatus,
                        safeQuery.getQualityStatus())
                .orderByDesc(WmsReceiveRecord::getId);
        return toPage(safeQuery, receiveRecordMapper, wrapper, wmsConvert::toReceiveRecordDTO);
    }

    @Override
    public ReceiveRecordDTO getReceiveRecord(final Long id) {
        return getById(id, receiveRecordMapper, wmsConvert::toReceiveRecordDTO, "ReceiveRecord");
    }

    @Override
    public PageResult<QualityFileDTO> pageQualityFiles(final WmsInboundQueryVO query) {
        final WmsInboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsQualityFile> wrapper = new LambdaQueryWrapper<WmsQualityFile>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsQualityFile::getQualityNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getInboundId() != null, WmsQualityFile::getInboundId, safeQuery.getInboundId())
                .eq(safeQuery.getSkuId() != null, WmsQualityFile::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getQualityResult() != null, WmsQualityFile::getQualityResult,
                        safeQuery.getQualityResult())
                .orderByDesc(WmsQualityFile::getId);
        return toPage(safeQuery, qualityFileMapper, wrapper, wmsConvert::toQualityFileDTO);
    }

    @Override
    public QualityFileDTO getQualityFile(final Long id) {
        return getById(id, qualityFileMapper, wmsConvert::toQualityFileDTO, "QualityFile");
    }

    @Override
    public PageResult<PutawayTaskDTO> pagePutawayTasks(final WmsInboundQueryVO query) {
        final WmsInboundQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsPutawayTask> wrapper = new LambdaQueryWrapper<WmsPutawayTask>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsPutawayTask::getPutawayNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getInboundId() != null, WmsPutawayTask::getInboundId, safeQuery.getInboundId())
                .eq(safeQuery.getSkuId() != null, WmsPutawayTask::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getWarehouseId() != null, WmsPutawayTask::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getStatus() != null, WmsPutawayTask::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsPutawayTask::getId);
        return toPage(safeQuery, putawayTaskMapper, wrapper, wmsConvert::toPutawayTaskDTO);
    }

    @Override
    public PutawayTaskDTO getPutawayTask(final Long id) {
        return getById(id, putawayTaskMapper, wmsConvert::toPutawayTaskDTO, "PutawayTask");
    }

    private WmsInboundQueryVO safeQuery(final WmsInboundQueryVO query) {
        return query == null ? new WmsInboundQueryVO() : query;
    }
}
