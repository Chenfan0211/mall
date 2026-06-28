package com.mall.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.supplier.dto.SupplierWorkbenchDTO;
import com.mall.api.supplier.vo.SupplierWorkbenchQueryVO;
import com.mall.supplier.entity.PurDeliveryItem;
import com.mall.supplier.entity.PurDeliveryOrder;
import com.mall.supplier.entity.PurPurchaseItem;
import com.mall.supplier.entity.PurPurchaseOrder;
import com.mall.supplier.mapper.PurDeliveryItemMapper;
import com.mall.supplier.mapper.PurDeliveryOrderMapper;
import com.mall.supplier.mapper.PurPurchaseItemMapper;
import com.mall.supplier.mapper.PurPurchaseOrderMapper;
import com.mall.supplier.service.SupplierWorkbenchService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SupplierWorkbenchServiceImpl implements SupplierWorkbenchService {

    private static final Long PURCHASE_WAIT_AUDIT = 10L;
    private static final Long PURCHASE_WAIT_DELIVERY = 30L;
    private static final Long DELIVERY_ON_ROAD = 20L;
    private static final Long DELIVERY_ARRIVED = 30L;

    private final PurPurchaseOrderMapper purchaseOrderMapper;
    private final PurPurchaseItemMapper purchaseItemMapper;
    private final PurDeliveryOrderMapper deliveryOrderMapper;
    private final PurDeliveryItemMapper deliveryItemMapper;

    public SupplierWorkbenchServiceImpl(
            final PurPurchaseOrderMapper purchaseOrderMapper,
            final PurPurchaseItemMapper purchaseItemMapper,
            final PurDeliveryOrderMapper deliveryOrderMapper,
            final PurDeliveryItemMapper deliveryItemMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseItemMapper = purchaseItemMapper;
        this.deliveryOrderMapper = deliveryOrderMapper;
        this.deliveryItemMapper = deliveryItemMapper;
    }

    @Override
    public SupplierWorkbenchDTO getWorkbench(final SupplierWorkbenchQueryVO query) {
        final SupplierWorkbenchQueryVO safeQuery = query == null ? new SupplierWorkbenchQueryVO() : query;
        final LocalDate deliveryDate = safeQuery.getDeliveryDate() == null
                ? LocalDate.now()
                : safeQuery.getDeliveryDate();
        final SupplierWorkbenchDTO result = new SupplierWorkbenchDTO();
        result.setSupplierId(safeQuery.getSupplierId());
        result.setWaitAuditPurchaseCount(countPurchases(safeQuery, PURCHASE_WAIT_AUDIT, null));
        result.setWaitDeliveryPurchaseCount(countPurchases(safeQuery, PURCHASE_WAIT_DELIVERY, null));
        result.setTodayDeliveryOrderCount(countDeliveries(safeQuery, null, deliveryDate));
        result.setDeliveringOrderCount(countDeliveries(safeQuery, DELIVERY_ON_ROAD, null));
        result.setArrivedOrderCount(countDeliveries(safeQuery, DELIVERY_ARRIVED, null));
        appendQtySummary(result, safeQuery);
        return result;
    }

    private Long countPurchases(
            final SupplierWorkbenchQueryVO query,
            final Long purchaseStatus,
            final LocalDate deliveryDate) {
        final LambdaQueryWrapper<PurPurchaseOrder> wrapper = new LambdaQueryWrapper<PurPurchaseOrder>()
                .eq(query.getSupplierId() != null, PurPurchaseOrder::getSupplierId, query.getSupplierId())
                .eq(query.getWarehouseId() != null, PurPurchaseOrder::getWarehouseId, query.getWarehouseId())
                .eq(query.getCityId() != null, PurPurchaseOrder::getCityId, query.getCityId())
                .eq(purchaseStatus != null, PurPurchaseOrder::getPurchaseStatus, purchaseStatus)
                .eq(deliveryDate != null, PurPurchaseOrder::getDeliveryDate, deliveryDate);
        return purchaseOrderMapper.selectCount(wrapper);
    }

    private Long countDeliveries(
            final SupplierWorkbenchQueryVO query,
            final Long status,
            final LocalDate deliveryDate) {
        final LambdaQueryWrapper<PurDeliveryOrder> wrapper = new LambdaQueryWrapper<PurDeliveryOrder>()
                .eq(query.getSupplierId() != null, PurDeliveryOrder::getSupplierId, query.getSupplierId())
                .eq(query.getWarehouseId() != null, PurDeliveryOrder::getWarehouseId, query.getWarehouseId())
                .eq(status != null, PurDeliveryOrder::getStatus, status);
        if (deliveryDate != null) {
            wrapper.ge(PurDeliveryOrder::getExpectedArrivalTime, deliveryDate.atStartOfDay())
                    .lt(PurDeliveryOrder::getExpectedArrivalTime, deliveryDate.plusDays(1L).atStartOfDay());
        }
        return deliveryOrderMapper.selectCount(wrapper);
    }

    private void appendQtySummary(final SupplierWorkbenchDTO result, final SupplierWorkbenchQueryVO query) {
        final List<Long> purchaseIds = listPurchaseIds(query);
        final List<PurPurchaseItem> purchaseItems = purchaseItemMapper.selectList(
                new LambdaQueryWrapper<PurPurchaseItem>()
                        .in(!purchaseIds.isEmpty(), PurPurchaseItem::getPurchaseId, purchaseIds)
                        .eq(purchaseIds.isEmpty() && query.getCityId() != null, PurPurchaseItem::getId, -1L)
                        .eq(query.getSupplierId() != null, PurPurchaseItem::getSupplierId, query.getSupplierId())
                        .eq(query.getWarehouseId() != null, PurPurchaseItem::getWarehouseId,
                                query.getWarehouseId()));
        result.setPurchaseQty(purchaseItems.stream()
                .map(PurPurchaseItem::getPurchaseQty)
                .map(this::zeroIfNull)
                .reduce(0L, Long::sum));
        result.setReceivedQty(purchaseItems.stream()
                .map(PurPurchaseItem::getReceivedQty)
                .map(this::zeroIfNull)
                .reduce(0L, Long::sum));
        final List<PurDeliveryItem> deliveryItems = deliveryItemMapper.selectList(
                new LambdaQueryWrapper<PurDeliveryItem>()
                        .in(!purchaseItems.isEmpty(), PurDeliveryItem::getPurchaseItemId,
                                purchaseItems.stream().map(PurPurchaseItem::getId).toList())
                        .eq(purchaseItems.isEmpty(), PurDeliveryItem::getId, -1L));
        result.setDiffQty(deliveryItems.stream()
                .map(PurDeliveryItem::getDiffQty)
                .map(this::zeroIfNull)
                .reduce(0L, Long::sum));
    }

    private List<Long> listPurchaseIds(final SupplierWorkbenchQueryVO query) {
        if (query.getCityId() == null) {
            return List.of();
        }
        return purchaseOrderMapper.selectList(new LambdaQueryWrapper<PurPurchaseOrder>()
                        .eq(PurPurchaseOrder::getCityId, query.getCityId())
                        .eq(query.getSupplierId() != null, PurPurchaseOrder::getSupplierId,
                                query.getSupplierId())
                        .eq(query.getWarehouseId() != null, PurPurchaseOrder::getWarehouseId,
                                query.getWarehouseId()))
                .stream()
                .map(PurPurchaseOrder::getId)
                .toList();
    }

    private Long zeroIfNull(final Long value) {
        return value == null ? 0L : value;
    }
}
