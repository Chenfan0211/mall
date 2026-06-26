package com.mall.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.supplier.dto.DeliveryItemDTO;
import com.mall.api.supplier.dto.DeliveryOrderDTO;
import com.mall.api.supplier.vo.DeliveryItemQueryVO;
import com.mall.api.supplier.vo.DeliveryOrderQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.supplier.convert.SupplierConvert;
import com.mall.supplier.entity.PurDeliveryItem;
import com.mall.supplier.entity.PurDeliveryOrder;
import com.mall.supplier.mapper.PurDeliveryItemMapper;
import com.mall.supplier.mapper.PurDeliveryOrderMapper;
import com.mall.supplier.service.DeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private final PurDeliveryOrderMapper deliveryOrderMapper;
    private final PurDeliveryItemMapper deliveryItemMapper;
    private final SupplierConvert supplierConvert;

    public DeliveryServiceImpl(
            final PurDeliveryOrderMapper deliveryOrderMapper,
            final PurDeliveryItemMapper deliveryItemMapper,
            final SupplierConvert supplierConvert) {
        this.deliveryOrderMapper = deliveryOrderMapper;
        this.deliveryItemMapper = deliveryItemMapper;
        this.supplierConvert = supplierConvert;
    }

    @Override
    public PageResult<DeliveryOrderDTO> pageDeliveryOrders(final DeliveryOrderQueryVO query) {
        final DeliveryOrderQueryVO safeQuery = query == null ? new DeliveryOrderQueryVO() : query;
        final LambdaQueryWrapper<PurDeliveryOrder> wrapper = new LambdaQueryWrapper<PurDeliveryOrder>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(PurDeliveryOrder::getDeliveryNo, safeQuery.getKeyword())
                        .or()
                        .like(PurDeliveryOrder::getPurchaseNo, safeQuery.getKeyword())
                        .or()
                        .like(PurDeliveryOrder::getVehicleNo, safeQuery.getKeyword()))
                .eq(safeQuery.getPurchaseId() != null, PurDeliveryOrder::getPurchaseId, safeQuery.getPurchaseId())
                .eq(StringUtils.hasText(safeQuery.getPurchaseNo()), PurDeliveryOrder::getPurchaseNo,
                        safeQuery.getPurchaseNo())
                .eq(safeQuery.getSupplierId() != null, PurDeliveryOrder::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getWarehouseId() != null, PurDeliveryOrder::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getStatus() != null, PurDeliveryOrder::getStatus, safeQuery.getStatus())
                .orderByDesc(PurDeliveryOrder::getId);
        final Page<PurDeliveryOrder> page = deliveryOrderMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toDeliveryOrderDTO)
                .toList());
    }

    @Override
    public DeliveryOrderDTO getDeliveryOrder(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "送货单ID不能为空");
        }
        final PurDeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "送货单不存在");
        }
        return supplierConvert.toDeliveryOrderDTO(deliveryOrder);
    }

    @Override
    public PageResult<DeliveryItemDTO> pageDeliveryItems(final DeliveryItemQueryVO query) {
        final DeliveryItemQueryVO safeQuery = query == null ? new DeliveryItemQueryVO() : query;
        final LambdaQueryWrapper<PurDeliveryItem> wrapper = new LambdaQueryWrapper<PurDeliveryItem>()
                .eq(safeQuery.getDeliveryId() != null, PurDeliveryItem::getDeliveryId, safeQuery.getDeliveryId())
                .eq(safeQuery.getPurchaseItemId() != null, PurDeliveryItem::getPurchaseItemId,
                        safeQuery.getPurchaseItemId())
                .eq(safeQuery.getSkuId() != null, PurDeliveryItem::getSkuId, safeQuery.getSkuId())
                .orderByDesc(PurDeliveryItem::getId);
        final Page<PurDeliveryItem> page = deliveryItemMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toDeliveryItemDTO)
                .toList());
    }

    @Override
    public DeliveryItemDTO getDeliveryItem(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "送货明细ID不能为空");
        }
        final PurDeliveryItem deliveryItem = deliveryItemMapper.selectById(id);
        if (deliveryItem == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "送货明细不存在");
        }
        return supplierConvert.toDeliveryItemDTO(deliveryItem);
    }
}
