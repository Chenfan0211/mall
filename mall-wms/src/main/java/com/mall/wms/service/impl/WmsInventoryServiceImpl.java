package com.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.wms.dto.InventoryDTO;
import com.mall.api.wms.dto.InventoryLockDTO;
import com.mall.api.wms.dto.InventoryLogDTO;
import com.mall.api.wms.dto.StocktakeOrderDTO;
import com.mall.api.wms.vo.WmsInventoryQueryVO;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsInventory;
import com.mall.wms.entity.WmsInventoryLock;
import com.mall.wms.entity.WmsInventoryLog;
import com.mall.wms.entity.WmsStocktakeOrder;
import com.mall.wms.mapper.WmsInventoryLockMapper;
import com.mall.wms.mapper.WmsInventoryLogMapper;
import com.mall.wms.mapper.WmsInventoryMapper;
import com.mall.wms.mapper.WmsStocktakeOrderMapper;
import com.mall.wms.service.WmsInventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class WmsInventoryServiceImpl extends WmsReadSupport implements WmsInventoryService {

    private final WmsInventoryMapper inventoryMapper;
    private final WmsInventoryLockMapper inventoryLockMapper;
    private final WmsInventoryLogMapper inventoryLogMapper;
    private final WmsStocktakeOrderMapper stocktakeOrderMapper;
    private final WmsConvert wmsConvert;

    public WmsInventoryServiceImpl(
            final WmsInventoryMapper inventoryMapper,
            final WmsInventoryLockMapper inventoryLockMapper,
            final WmsInventoryLogMapper inventoryLogMapper,
            final WmsStocktakeOrderMapper stocktakeOrderMapper,
            final WmsConvert wmsConvert) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryLockMapper = inventoryLockMapper;
        this.inventoryLogMapper = inventoryLogMapper;
        this.stocktakeOrderMapper = stocktakeOrderMapper;
        this.wmsConvert = wmsConvert;
    }

    @Override
    public PageResult<InventoryDTO> pageInventories(final WmsInventoryQueryVO query) {
        final WmsInventoryQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsInventory> wrapper = new LambdaQueryWrapper<WmsInventory>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsInventory::getBatchNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getWarehouseId() != null, WmsInventory::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getZoneId() != null, WmsInventory::getZoneId, safeQuery.getZoneId())
                .eq(safeQuery.getLocationId() != null, WmsInventory::getLocationId, safeQuery.getLocationId())
                .eq(safeQuery.getSkuId() != null, WmsInventory::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getSupplierId() != null, WmsInventory::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getInventoryStatus() != null, WmsInventory::getInventoryStatus,
                        safeQuery.getInventoryStatus())
                .orderByDesc(WmsInventory::getId);
        return toPage(safeQuery, inventoryMapper, wrapper, wmsConvert::toInventoryDTO);
    }

    @Override
    public InventoryDTO getInventory(final Long id) {
        return getById(id, inventoryMapper, wmsConvert::toInventoryDTO, "Inventory");
    }

    @Override
    public PageResult<InventoryLockDTO> pageInventoryLocks(final WmsInventoryQueryVO query) {
        final WmsInventoryQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsInventoryLock> wrapper = new LambdaQueryWrapper<WmsInventoryLock>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsInventoryLock::getLockNo, safeQuery.getKeyword())
                        .or()
                        .like(WmsInventoryLock::getBizNo, safeQuery.getKeyword()))
                .eq(safeQuery.getWarehouseId() != null, WmsInventoryLock::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getSkuId() != null, WmsInventoryLock::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getBizType() != null, WmsInventoryLock::getBizType, safeQuery.getBizType())
                .eq(StringUtils.hasText(safeQuery.getBizNo()), WmsInventoryLock::getBizNo, safeQuery.getBizNo())
                .eq(safeQuery.getLockStatus() != null, WmsInventoryLock::getLockStatus,
                        safeQuery.getLockStatus())
                .orderByDesc(WmsInventoryLock::getId);
        return toPage(safeQuery, inventoryLockMapper, wrapper, wmsConvert::toInventoryLockDTO);
    }

    @Override
    public InventoryLockDTO getInventoryLock(final Long id) {
        return getById(id, inventoryLockMapper, wmsConvert::toInventoryLockDTO, "InventoryLock");
    }

    @Override
    public PageResult<InventoryLogDTO> pageInventoryLogs(final WmsInventoryQueryVO query) {
        final WmsInventoryQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsInventoryLog> wrapper = new LambdaQueryWrapper<WmsInventoryLog>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsInventoryLog::getLogNo, safeQuery.getKeyword())
                        .or()
                        .like(WmsInventoryLog::getBizNo, safeQuery.getKeyword()))
                .eq(safeQuery.getWarehouseId() != null, WmsInventoryLog::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getZoneId() != null, WmsInventoryLog::getZoneId, safeQuery.getZoneId())
                .eq(safeQuery.getLocationId() != null, WmsInventoryLog::getLocationId,
                        safeQuery.getLocationId())
                .eq(safeQuery.getSkuId() != null, WmsInventoryLog::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getChangeType() != null, WmsInventoryLog::getChangeType,
                        safeQuery.getChangeType())
                .eq(StringUtils.hasText(safeQuery.getBizNo()), WmsInventoryLog::getBizNo, safeQuery.getBizNo())
                .orderByDesc(WmsInventoryLog::getId);
        return toPage(safeQuery, inventoryLogMapper, wrapper, wmsConvert::toInventoryLogDTO);
    }

    @Override
    public InventoryLogDTO getInventoryLog(final Long id) {
        return getById(id, inventoryLogMapper, wmsConvert::toInventoryLogDTO, "InventoryLog");
    }

    @Override
    public PageResult<StocktakeOrderDTO> pageStocktakeOrders(final WmsInventoryQueryVO query) {
        final WmsInventoryQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsStocktakeOrder> wrapper = new LambdaQueryWrapper<WmsStocktakeOrder>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), WmsStocktakeOrder::getStocktakeNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getWarehouseId() != null, WmsStocktakeOrder::getWarehouseId,
                        safeQuery.getWarehouseId())
                .eq(safeQuery.getSkuId() != null, WmsStocktakeOrder::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getStatus() != null, WmsStocktakeOrder::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsStocktakeOrder::getId);
        return toPage(safeQuery, stocktakeOrderMapper, wrapper, wmsConvert::toStocktakeOrderDTO);
    }

    @Override
    public StocktakeOrderDTO getStocktakeOrder(final Long id) {
        return getById(id, stocktakeOrderMapper, wmsConvert::toStocktakeOrderDTO, "StocktakeOrder");
    }

    private WmsInventoryQueryVO safeQuery(final WmsInventoryQueryVO query) {
        return query == null ? new WmsInventoryQueryVO() : query;
    }
}
