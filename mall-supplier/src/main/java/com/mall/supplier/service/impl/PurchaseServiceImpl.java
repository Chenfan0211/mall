package com.mall.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.supplier.dto.PurchaseAuditLogDTO;
import com.mall.api.supplier.dto.PurchaseItemDTO;
import com.mall.api.supplier.dto.PurchaseOrderDTO;
import com.mall.api.supplier.dto.PurchaseSourceLogDTO;
import com.mall.api.supplier.vo.PurchaseItemQueryVO;
import com.mall.api.supplier.vo.PurchaseOrderQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.supplier.convert.SupplierConvert;
import com.mall.supplier.entity.PurPurchaseAuditLog;
import com.mall.supplier.entity.PurPurchaseItem;
import com.mall.supplier.entity.PurPurchaseOrder;
import com.mall.supplier.entity.PurPurchaseSourceLog;
import com.mall.supplier.mapper.PurPurchaseAuditLogMapper;
import com.mall.supplier.mapper.PurPurchaseItemMapper;
import com.mall.supplier.mapper.PurPurchaseOrderMapper;
import com.mall.supplier.mapper.PurPurchaseSourceLogMapper;
import com.mall.supplier.service.PurchaseService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class PurchaseServiceImpl implements PurchaseService {

    private final PurPurchaseOrderMapper purchaseOrderMapper;
    private final PurPurchaseItemMapper purchaseItemMapper;
    private final PurPurchaseAuditLogMapper auditLogMapper;
    private final PurPurchaseSourceLogMapper sourceLogMapper;
    private final SupplierConvert supplierConvert;

    public PurchaseServiceImpl(
            final PurPurchaseOrderMapper purchaseOrderMapper,
            final PurPurchaseItemMapper purchaseItemMapper,
            final PurPurchaseAuditLogMapper auditLogMapper,
            final PurPurchaseSourceLogMapper sourceLogMapper,
            final SupplierConvert supplierConvert) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseItemMapper = purchaseItemMapper;
        this.auditLogMapper = auditLogMapper;
        this.sourceLogMapper = sourceLogMapper;
        this.supplierConvert = supplierConvert;
    }

    @Override
    public PageResult<PurchaseOrderDTO> pagePurchaseOrders(final PurchaseOrderQueryVO query) {
        final PurchaseOrderQueryVO safeQuery = query == null ? new PurchaseOrderQueryVO() : query;
        final LambdaQueryWrapper<PurPurchaseOrder> wrapper = new LambdaQueryWrapper<PurPurchaseOrder>()
                .like(StringUtils.hasText(safeQuery.getKeyword()), PurPurchaseOrder::getPurchaseNo,
                        safeQuery.getKeyword())
                .eq(safeQuery.getSourceType() != null, PurPurchaseOrder::getSourceType, safeQuery.getSourceType())
                .eq(safeQuery.getSupplierId() != null, PurPurchaseOrder::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getWarehouseId() != null, PurPurchaseOrder::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getCityId() != null, PurPurchaseOrder::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getPurchaseStatus() != null, PurPurchaseOrder::getPurchaseStatus,
                        safeQuery.getPurchaseStatus())
                .eq(safeQuery.getAuditStatus() != null, PurPurchaseOrder::getAuditStatus, safeQuery.getAuditStatus())
                .eq(safeQuery.getDeliveryDate() != null, PurPurchaseOrder::getDeliveryDate,
                        safeQuery.getDeliveryDate())
                .orderByDesc(PurPurchaseOrder::getId);
        final Page<PurPurchaseOrder> page = purchaseOrderMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toPurchaseOrderDTO)
                .toList());
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrder(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "采购单ID不能为空");
        }
        final PurPurchaseOrder purchaseOrder = purchaseOrderMapper.selectById(id);
        if (purchaseOrder == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "采购单不存在");
        }
        return supplierConvert.toPurchaseOrderDTO(purchaseOrder);
    }

    @Override
    public PageResult<PurchaseItemDTO> pagePurchaseItems(final PurchaseItemQueryVO query) {
        final PurchaseItemQueryVO safeQuery = query == null ? new PurchaseItemQueryVO() : query;
        final LambdaQueryWrapper<PurPurchaseItem> wrapper = new LambdaQueryWrapper<PurPurchaseItem>()
                .eq(safeQuery.getPurchaseId() != null, PurPurchaseItem::getPurchaseId, safeQuery.getPurchaseId())
                .eq(StringUtils.hasText(safeQuery.getPurchaseNo()), PurPurchaseItem::getPurchaseNo,
                        safeQuery.getPurchaseNo())
                .eq(safeQuery.getProductId() != null, PurPurchaseItem::getProductId, safeQuery.getProductId())
                .eq(safeQuery.getSkuId() != null, PurPurchaseItem::getSkuId, safeQuery.getSkuId())
                .eq(safeQuery.getSupplierId() != null, PurPurchaseItem::getSupplierId, safeQuery.getSupplierId())
                .eq(safeQuery.getWarehouseId() != null, PurPurchaseItem::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getStatus() != null, PurPurchaseItem::getStatus, safeQuery.getStatus())
                .orderByDesc(PurPurchaseItem::getId);
        final Page<PurPurchaseItem> page = purchaseItemMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream()
                .map(supplierConvert::toPurchaseItemDTO)
                .toList());
    }

    @Override
    public PurchaseItemDTO getPurchaseItem(final Long id) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "采购明细ID不能为空");
        }
        final PurPurchaseItem purchaseItem = purchaseItemMapper.selectById(id);
        if (purchaseItem == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "采购明细不存在");
        }
        return supplierConvert.toPurchaseItemDTO(purchaseItem);
    }

    @Override
    public List<PurchaseAuditLogDTO> listAuditLogs(final Long purchaseId) {
        if (purchaseId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "采购单ID不能为空");
        }
        final LambdaQueryWrapper<PurPurchaseAuditLog> wrapper = new LambdaQueryWrapper<PurPurchaseAuditLog>()
                .eq(PurPurchaseAuditLog::getPurchaseId, purchaseId)
                .orderByAsc(PurPurchaseAuditLog::getCreateTime)
                .orderByAsc(PurPurchaseAuditLog::getId);
        return auditLogMapper.selectList(wrapper).stream()
                .map(supplierConvert::toPurchaseAuditLogDTO)
                .toList();
    }

    @Override
    public List<PurchaseSourceLogDTO> listSourceLogs(final Long purchaseId) {
        if (purchaseId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "采购单ID不能为空");
        }
        final LambdaQueryWrapper<PurPurchaseSourceLog> wrapper = new LambdaQueryWrapper<PurPurchaseSourceLog>()
                .eq(PurPurchaseSourceLog::getPurchaseId, purchaseId)
                .orderByAsc(PurPurchaseSourceLog::getCreateTime)
                .orderByAsc(PurPurchaseSourceLog::getId);
        return sourceLogMapper.selectList(wrapper).stream()
                .map(supplierConvert::toPurchaseSourceLogDTO)
                .toList();
    }
}
