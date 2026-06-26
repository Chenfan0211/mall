package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.user.vo.UserAfterSaleApplyVO;
import com.mall.api.user.dto.UserReturnRecordDTO;
import com.mall.api.user.vo.UserAfterSaleQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.AfsAfterSale;
import com.mall.user.entity.AfsAfterSaleItem;
import com.mall.user.entity.AfsReturnRecord;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.OrdOrderItem;
import com.mall.user.mapper.AfsAfterSaleItemMapper;
import com.mall.user.mapper.AfsAfterSaleMapper;
import com.mall.user.mapper.AfsReturnRecordMapper;
import com.mall.user.mapper.OrdOrderItemMapper;
import com.mall.user.mapper.OrdOrderMapper;
import com.mall.user.service.UserAfterSaleService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserAfterSaleServiceImpl extends UserReadSupport implements UserAfterSaleService {

    private static final Long AFTER_SALE_WAIT_AUDIT = 10L;
    private static final Long AFTER_SALE_PENDING = 10L;
    private static final Long TRADE_PAID = 20L;
    private static final Long TRADE_COMPLETED = 50L;
    private static final Long FULFILL_WAIT_PURCHASE = 20L;
    private static final DateTimeFormatter NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final AfsAfterSaleMapper afterSaleMapper;
    private final AfsAfterSaleItemMapper afterSaleItemMapper;
    private final AfsReturnRecordMapper returnRecordMapper;
    private final OrdOrderMapper orderMapper;
    private final OrdOrderItemMapper orderItemMapper;
    private final UserConvert userConvert;

    public UserAfterSaleServiceImpl(
            final AfsAfterSaleMapper afterSaleMapper,
            final AfsAfterSaleItemMapper afterSaleItemMapper,
            final AfsReturnRecordMapper returnRecordMapper,
            final OrdOrderMapper orderMapper,
            final OrdOrderItemMapper orderItemMapper,
            final UserConvert userConvert) {
        this.afterSaleMapper = afterSaleMapper;
        this.afterSaleItemMapper = afterSaleItemMapper;
        this.returnRecordMapper = returnRecordMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.userConvert = userConvert;
    }

    @Override
    public PageResult<AfterSaleDTO> pageAfterSales(final UserAfterSaleQueryVO query) {
        final UserAfterSaleQueryVO safeQuery = validateQuery(query);
        final LambdaQueryWrapper<AfsAfterSale> wrapper = new LambdaQueryWrapper<AfsAfterSale>()
                .eq(AfsAfterSale::getUserId, safeQuery.getUserId())
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(AfsAfterSale::getAfterSaleNo, safeQuery.getKeyword())
                        .or()
                        .like(AfsAfterSale::getOrderNo, safeQuery.getKeyword())
                        .or()
                        .like(AfsAfterSale::getApplyReason, safeQuery.getKeyword()))
                .eq(safeQuery.getAfterSaleType() != null, AfsAfterSale::getAfterSaleType,
                        safeQuery.getAfterSaleType())
                .eq(safeQuery.getStatus() != null, AfsAfterSale::getStatus, safeQuery.getStatus())
                .orderByDesc(AfsAfterSale::getId);
        return toPage(safeQuery, afterSaleMapper, wrapper, userConvert::toAfterSaleDTO);
    }

    @Override
    @Transactional
    public AfterSaleDTO applyAfterSale(final UserAfterSaleApplyVO request) {
        validateApplyRequest(request);
        final OrdOrderItem item = orderItemMapper.selectById(request.getOrderItemId());
        if (item == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "订单商品不存在");
        }
        final OrdOrder order = orderMapper.selectById(item.getOrderId());
        if (order == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "订单不存在");
        }
        if (!request.getUserId().equals(order.getUserId())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN, "不能申请其他用户订单售后");
        }
        if (!Objects.equals(order.getTradeStatus(), TRADE_PAID) && !Objects.equals(order.getTradeStatus(),
                TRADE_COMPLETED)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "当前订单状态不能申请售后");
        }
        if (item.getFulfillStatus() == null || item.getFulfillStatus() < FULFILL_WAIT_PURCHASE) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "订单商品尚未进入履约，不能申请售后");
        }
        final Long remainQty = safeQty(item.getQty()) - safeQty(item.getRefundedQty());
        if (remainQty < request.getApplyQty()) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "申请数量超过可售后数量");
        }
        if (item.getSalePrice() == null) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "订单商品价格异常，不能申请售后");
        }
        final BigDecimal maxRefundAmount = item.getSalePrice().multiply(BigDecimal.valueOf(request.getApplyQty()));
        if (request.getRefundAmount().compareTo(maxRefundAmount) > 0) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "退款金额不能超过商品可退金额");
        }
        final LocalDateTime now = LocalDateTime.now();
        final AfsAfterSale afterSale = buildAfterSale(order, request, now);
        afterSaleMapper.insert(afterSale);
        insertAfterSaleItem(afterSale, item, request, now);
        item.setAfterSaleStatus(AFTER_SALE_PENDING);
        prepareUpdate(item, now);
        orderItemMapper.updateById(item);
        return userConvert.toAfterSaleDTO(afterSale);
    }

    @Override
    public AfterSaleDTO getAfterSale(final Long id, final Long userId) {
        return userConvert.toAfterSaleDTO(loadUserAfterSale(id, userId));
    }

    @Override
    public List<AfterSaleItemDTO> listAfterSaleItems(final Long afterSaleId, final Long userId) {
        loadUserAfterSale(afterSaleId, userId);
        return afterSaleItemMapper.selectList(new LambdaQueryWrapper<AfsAfterSaleItem>()
                        .eq(AfsAfterSaleItem::getAfterSaleId, afterSaleId)
                        .orderByDesc(AfsAfterSaleItem::getId))
                .stream()
                .map(userConvert::toAfterSaleItemDTO)
                .toList();
    }

    @Override
    public List<UserReturnRecordDTO> listReturnRecords(final Long afterSaleId, final Long userId) {
        loadUserAfterSale(afterSaleId, userId);
        return returnRecordMapper.selectList(new LambdaQueryWrapper<AfsReturnRecord>()
                        .eq(AfsReturnRecord::getAfterSaleId, afterSaleId)
                        .orderByDesc(AfsReturnRecord::getId))
                .stream()
                .map(userConvert::toReturnRecordDTO)
                .toList();
    }

    private UserAfterSaleQueryVO validateQuery(final UserAfterSaleQueryVO query) {
        if (query == null || query.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        return query;
    }

    private void validateApplyRequest(final UserAfterSaleApplyVO request) {
        if (request == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "售后申请不能为空");
        }
        if (request.getUserId() == null || request.getOrderItemId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID和订单商品ID不能为空");
        }
        if (request.getAfterSaleType() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "售后类型不能为空");
        }
        if (request.getApplyQty() == null || request.getApplyQty() <= 0L) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "申请数量必须大于0");
        }
        if (request.getRefundAmount() == null || request.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "退款金额必须大于0");
        }
        if (!StringUtils.hasText(request.getApplyReason())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "申请原因不能为空");
        }
    }

    private AfsAfterSale buildAfterSale(
            final OrdOrder order,
            final UserAfterSaleApplyVO request,
            final LocalDateTime now) {
        final AfsAfterSale afterSale = new AfsAfterSale();
        prepareCreate(afterSale, IdWorker.getId(), now);
        afterSale.setAfterSaleNo(buildNo(now));
        afterSale.setOrderId(order.getId());
        afterSale.setOrderNo(order.getOrderNo());
        afterSale.setUserId(order.getUserId());
        afterSale.setCityId(order.getCityId());
        afterSale.setStationId(order.getStationId());
        afterSale.setAfterSaleType(request.getAfterSaleType());
        afterSale.setStatus(AFTER_SALE_WAIT_AUDIT);
        afterSale.setApplyReason(request.getApplyReason());
        afterSale.setRefundAmount(request.getRefundAmount());
        afterSale.setResponsibilityType(null);
        return afterSale;
    }

    private void insertAfterSaleItem(
            final AfsAfterSale afterSale,
            final OrdOrderItem item,
            final UserAfterSaleApplyVO request,
            final LocalDateTime now) {
        final AfsAfterSaleItem afterSaleItem = new AfsAfterSaleItem();
        prepareCreate(afterSaleItem, IdWorker.getId(), now);
        afterSaleItem.setAfterSaleId(afterSale.getId());
        afterSaleItem.setOrderItemId(item.getId());
        afterSaleItem.setSkuId(item.getSkuId());
        afterSaleItem.setSupplierId(item.getSupplierId());
        afterSaleItem.setApplyQty(request.getApplyQty());
        afterSaleItem.setApprovedQty(0L);
        afterSaleItem.setRefundAmount(request.getRefundAmount());
        afterSaleItemMapper.insert(afterSaleItem);
    }

    private String buildNo(final LocalDateTime now) {
        return "AFS" + now.format(NO_TIME_FORMATTER) + IdWorker.getIdStr();
    }

    private Long safeQty(final Long qty) {
        return qty == null ? 0L : qty;
    }

    private AfsAfterSale loadUserAfterSale(final Long id, final Long userId) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "售后单ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        final AfsAfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "售后单不存在");
        }
        if (!userId.equals(afterSale.getUserId())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN, "不能查看其他用户售后单");
        }
        return afterSale;
    }
}
