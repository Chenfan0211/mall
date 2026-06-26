package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.user.vo.UserOrderCancelVO;
import com.mall.api.user.vo.UserOrderQueryVO;
import com.mall.api.user.vo.UserOrderSubmitVO;
import com.mall.api.user.vo.UserPaymentCreateVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.OrdCart;
import com.mall.user.entity.OrdFulfillmentTrack;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.OrdOrderItem;
import com.mall.user.entity.OrdOrderStatusLog;
import com.mall.user.entity.PayTrade;
import com.mall.user.entity.PrdProduct;
import com.mall.user.entity.PrdSku;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.mapper.OrdFulfillmentTrackMapper;
import com.mall.user.mapper.OrdCartMapper;
import com.mall.user.mapper.OrdOrderItemMapper;
import com.mall.user.mapper.OrdOrderMapper;
import com.mall.user.mapper.OrdOrderStatusLogMapper;
import com.mall.user.mapper.PayTradeMapper;
import com.mall.user.mapper.PrdProductMapper;
import com.mall.user.mapper.PrdSkuMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
import com.mall.user.service.UserOrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserOrderServiceImpl extends UserReadSupport implements UserOrderService {

    private static final Long CART_SELECTED = 1L;
    private static final Long CART_VALID = 1L;
    private static final Long CART_INVALID = 2L;
    private static final Long TRADE_WAIT_PAY = 10L;
    private static final Long TRADE_CLOSED = 30L;
    private static final Long PAY_UNPAID = 10L;
    private static final Long FULFILL_WAIT_CUTOFF = 10L;
    private static final Long PERIOD_ONLINE = 20L;
    private static final Long PUBLISH_SKU_ENABLED = 1L;
    private static final Long STATUS_LINE_TRADE = 1L;
    private static final Long OPERATOR_USER = 1L;
    private static final Long PAY_TRADE_WAIT = 10L;
    private static final Long PAY_TRADE_CLOSED = 50L;
    private static final DateTimeFormatter NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrdOrderMapper orderMapper;
    private final OrdOrderItemMapper orderItemMapper;
    private final OrdFulfillmentTrackMapper fulfillmentTrackMapper;
    private final OrdCartMapper cartMapper;
    private final SalePublishPeriodMapper periodMapper;
    private final SalePublishSkuMapper publishSkuMapper;
    private final PrdProductMapper productMapper;
    private final PrdSkuMapper skuMapper;
    private final OrdOrderStatusLogMapper orderStatusLogMapper;
    private final PayTradeMapper payTradeMapper;
    private final UserConvert userConvert;

    public UserOrderServiceImpl(
            final OrdOrderMapper orderMapper,
            final OrdOrderItemMapper orderItemMapper,
            final OrdFulfillmentTrackMapper fulfillmentTrackMapper,
            final OrdCartMapper cartMapper,
            final SalePublishPeriodMapper periodMapper,
            final SalePublishSkuMapper publishSkuMapper,
            final PrdProductMapper productMapper,
            final PrdSkuMapper skuMapper,
            final OrdOrderStatusLogMapper orderStatusLogMapper,
            final PayTradeMapper payTradeMapper,
            final UserConvert userConvert) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.fulfillmentTrackMapper = fulfillmentTrackMapper;
        this.cartMapper = cartMapper;
        this.periodMapper = periodMapper;
        this.publishSkuMapper = publishSkuMapper;
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.orderStatusLogMapper = orderStatusLogMapper;
        this.payTradeMapper = payTradeMapper;
        this.userConvert = userConvert;
    }

    @Override
    public PageResult<OrderDTO> pageOrders(final UserOrderQueryVO query) {
        final UserOrderQueryVO safeQuery = validateQuery(query);
        final LambdaQueryWrapper<OrdOrder> wrapper = new LambdaQueryWrapper<OrdOrder>()
                .eq(OrdOrder::getUserId, safeQuery.getUserId())
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(OrdOrder::getOrderNo, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrder::getPickupName, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrder::getPickupMobile, safeQuery.getKeyword()))
                .eq(safeQuery.getTradeStatus() != null, OrdOrder::getTradeStatus, safeQuery.getTradeStatus())
                .eq(safeQuery.getPayStatus() != null, OrdOrder::getPayStatus, safeQuery.getPayStatus())
                .eq(safeQuery.getFulfillStatus() != null, OrdOrder::getFulfillStatus,
                        safeQuery.getFulfillStatus())
                .orderByDesc(OrdOrder::getId);
        return toPage(safeQuery, orderMapper, wrapper, userConvert::toOrderDTO);
    }

    @Override
    @Transactional
    public OrderDTO submitOrder(final UserOrderSubmitVO request) {
        validateSubmitRequest(request);
        final OrdOrder exists = orderMapper.selectOne(new LambdaQueryWrapper<OrdOrder>()
                .eq(OrdOrder::getIdempotentKey, request.getIdempotentKey()));
        if (exists != null) {
            if (!request.getUserId().equals(exists.getUserId())) {
                throw new BusinessException(CommonErrorCode.FORBIDDEN, "不能复用其他用户下单幂等键");
            }
            return userConvert.toOrderDTO(exists);
        }
        final List<OrdCart> carts = listOrderCarts(request);
        final LocalDateTime now = LocalDateTime.now();
        final Long orderId = IdWorker.getId();
        final String orderNo = buildNo("ORD", now);
        final OrdOrder order = buildOrder(request, carts, orderId, orderNo, now);
        orderMapper.insert(order);
        insertOrderItems(order, carts, now);
        invalidateCarts(carts, now);
        insertStatusLog(order.getId(), null, null, STATUS_LINE_TRADE, TRADE_WAIT_PAY, "用户提交订单", request.getUserId(),
                now);
        return userConvert.toOrderDTO(order);
    }

    @Override
    public OrderDTO getOrder(final Long id, final Long userId) {
        return userConvert.toOrderDTO(loadUserOrder(id, userId));
    }

    @Override
    @Transactional
    public OrderDTO cancelWaitPayOrder(final Long id, final UserOrderCancelVO request) {
        if (request == null || request.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        if (!StringUtils.hasText(request.getCancelReason())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "取消原因不能为空");
        }
        final OrdOrder order = loadUserOrder(id, request.getUserId());
        if (Objects.equals(order.getTradeStatus(), TRADE_CLOSED)) {
            return userConvert.toOrderDTO(order);
        }
        if (!Objects.equals(order.getTradeStatus(), TRADE_WAIT_PAY) || !Objects.equals(order.getPayStatus(),
                PAY_UNPAID)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "只有未支付订单才能用户取消");
        }
        final LocalDateTime now = LocalDateTime.now();
        final Long beforeTradeStatus = order.getTradeStatus();
        order.setTradeStatus(TRADE_CLOSED);
        order.setPayStatus(PAY_UNPAID);
        order.setCancelReason(request.getCancelReason());
        prepareUpdate(order, now);
        orderMapper.updateById(order);
        closeWaitingPayments(order, now);
        insertStatusLog(order.getId(), null, beforeTradeStatus, STATUS_LINE_TRADE, TRADE_CLOSED,
                request.getCancelReason(), request.getUserId(), now);
        return userConvert.toOrderDTO(order);
    }

    @Override
    public PageResult<OrderItemDTO> pageOrderItems(final UserOrderQueryVO query) {
        final UserOrderQueryVO safeQuery = validateQuery(query);
        final List<Long> orderIds = listUserOrderIds(safeQuery.getUserId());
        if (orderIds.isEmpty()) {
            return PageResult.of(0L, List.of());
        }
        final LambdaQueryWrapper<OrdOrderItem> wrapper = new LambdaQueryWrapper<OrdOrderItem>()
                .in(OrdOrderItem::getOrderId, orderIds)
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(OrdOrderItem::getOrderNo, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrderItem::getOrderItemNo, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrderItem::getProductName, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrderItem::getSkuName, safeQuery.getKeyword()))
                .eq(safeQuery.getFulfillStatus() != null, OrdOrderItem::getFulfillStatus,
                        safeQuery.getFulfillStatus())
                .orderByDesc(OrdOrderItem::getId);
        return toPage(safeQuery, orderItemMapper, wrapper, userConvert::toOrderItemDTO);
    }

    @Override
    public List<FulfillmentTrackDTO> listFulfillmentTracks(final Long orderId, final Long userId) {
        loadUserOrder(orderId, userId);
        return fulfillmentTrackMapper.selectList(new LambdaQueryWrapper<OrdFulfillmentTrack>()
                        .eq(OrdFulfillmentTrack::getOrderId, orderId)
                        .orderByAsc(OrdFulfillmentTrack::getId))
                .stream()
                .map(userConvert::toFulfillmentTrackDTO)
                .toList();
    }

    @Override
    @Transactional
    public PaymentTradeDTO createPaymentTrade(final Long orderId, final UserPaymentCreateVO request) {
        if (request == null || request.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        if (request.getChannelType() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "支付渠道不能为空");
        }
        if (!StringUtils.hasText(request.getIdempotentKey())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "幂等键不能为空");
        }
        final OrdOrder order = loadUserOrder(orderId, request.getUserId());
        if (!Objects.equals(order.getTradeStatus(), TRADE_WAIT_PAY) || !Objects.equals(order.getPayStatus(),
                PAY_UNPAID)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "只有未支付订单才能发起支付");
        }
        if (order.getExpireTime() != null && order.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "订单已超过支付时间");
        }
        final PayTrade exists = payTradeMapper.selectOne(new LambdaQueryWrapper<PayTrade>()
                .eq(PayTrade::getIdempotentKey, request.getIdempotentKey()));
        if (exists != null) {
            if (!order.getId().equals(exists.getOrderId())) {
                throw new BusinessException(CommonErrorCode.FORBIDDEN, "不能复用其他订单支付幂等键");
            }
            return userConvert.toPaymentTradeDTO(exists);
        }
        final LocalDateTime now = LocalDateTime.now();
        final PayTrade payTrade = new PayTrade();
        prepareCreate(payTrade, IdWorker.getId(), now);
        payTrade.setPayNo(buildNo("PAY", now));
        payTrade.setOrderId(order.getId());
        payTrade.setOrderNo(order.getOrderNo());
        payTrade.setChannelType(request.getChannelType());
        payTrade.setPayAmount(order.getPayAmount());
        payTrade.setTradeStatus(PAY_TRADE_WAIT);
        payTrade.setIdempotentKey(request.getIdempotentKey());
        payTradeMapper.insert(payTrade);
        return userConvert.toPaymentTradeDTO(payTrade);
    }

    private UserOrderQueryVO validateQuery(final UserOrderQueryVO query) {
        if (query == null || query.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        return query;
    }

    private OrdOrder loadUserOrder(final Long id, final Long userId) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "订单ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        final OrdOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN, "不能查看其他用户订单");
        }
        return order;
    }

    private void validateSubmitRequest(final UserOrderSubmitVO request) {
        if (request == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "订单提交参数不能为空");
        }
        if (request.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        if (request.getCityId() == null || request.getStationId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "城市和自提点不能为空");
        }
        if (!StringUtils.hasText(request.getPickupName()) || !StringUtils.hasText(request.getPickupMobile())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "提货人信息不能为空");
        }
        if (!StringUtils.hasText(request.getIdempotentKey())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "幂等键不能为空");
        }
    }

    private List<OrdCart> listOrderCarts(final UserOrderSubmitVO request) {
        final LambdaQueryWrapper<OrdCart> wrapper = new LambdaQueryWrapper<OrdCart>()
                .eq(OrdCart::getUserId, request.getUserId())
                .eq(OrdCart::getCityId, request.getCityId())
                .eq(OrdCart::getStationId, request.getStationId())
                .eq(OrdCart::getSelectedFlag, CART_SELECTED)
                .eq(OrdCart::getValidStatus, CART_VALID)
                .in(!CollectionUtils.isEmpty(request.getCartItemIds()), OrdCart::getId, request.getCartItemIds());
        final List<OrdCart> carts = cartMapper.selectList(wrapper);
        if (carts.isEmpty()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "请选择要结算的购物车商品");
        }
        if (!CollectionUtils.isEmpty(request.getCartItemIds()) && carts.size() != request.getCartItemIds().size()) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "购物车商品状态已变化");
        }
        return carts;
    }

    private OrdOrder buildOrder(
            final UserOrderSubmitVO request,
            final List<OrdCart> carts,
            final Long orderId,
            final String orderNo,
            final LocalDateTime now) {
        final BigDecimal totalAmount = carts.stream()
                .map(this::calculateCartAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final SalePublishPeriod period = periodMapper.selectById(carts.get(0).getPeriodId());
        final OrdOrder order = new OrdOrder();
        prepareCreate(order, orderId, now);
        order.setOrderNo(orderNo);
        order.setUserId(request.getUserId());
        order.setRegionId(request.getRegionId() == null ? 0L : request.getRegionId());
        order.setCityId(request.getCityId());
        order.setStationId(request.getStationId());
        order.setLeaderId(request.getLeaderId());
        order.setPickupName(request.getPickupName());
        order.setPickupMobile(request.getPickupMobile());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setTradeStatus(TRADE_WAIT_PAY);
        order.setPayStatus(PAY_UNPAID);
        order.setFulfillStatus(FULFILL_WAIT_CUTOFF);
        order.setExpireTime(now.plusMinutes(15L));
        order.setIdempotentKey(request.getIdempotentKey());
        if (period != null && period.getRegionId() != null && request.getRegionId() == null) {
            order.setRegionId(period.getRegionId());
        }
        return order;
    }

    private void insertOrderItems(final OrdOrder order, final List<OrdCart> carts, final LocalDateTime now) {
        Long lineNo = 1L;
        for (final OrdCart cart : carts) {
            validateCartForOrder(cart, now);
            final SalePublishSku publishSku = loadPublishSku(cart);
            final PrdProduct product = productMapper.selectById(cart.getProductId());
            final PrdSku sku = skuMapper.selectById(cart.getSkuId());
            final SalePublishPeriod period = periodMapper.selectById(cart.getPeriodId());
            final OrdOrderItem item = new OrdOrderItem();
            prepareCreate(item, IdWorker.getId(), now);
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo());
            item.setOrderItemNo(order.getOrderNo() + "-" + lineNo);
            item.setPeriodId(cart.getPeriodId());
            item.setProductId(cart.getProductId());
            item.setSkuId(cart.getSkuId());
            item.setSupplierId(publishSku.getSupplierId());
            item.setWarehouseId(period == null || period.getWarehouseId() == null ? 0L : period.getWarehouseId());
            item.setCityId(order.getCityId());
            item.setStationId(order.getStationId());
            item.setProductName(product == null ? "未知商品" : product.getProductName());
            item.setSkuName(sku == null ? "默认规格" : sku.getSkuName());
            item.setSalePrice(publishSku.getSalePrice());
            item.setQty(safeQty(cart.getQty()));
            item.setPickedQty(0L);
            item.setRefundedQty(0L);
            item.setItemAmount(publishSku.getSalePrice().multiply(BigDecimal.valueOf(safeQty(cart.getQty()))));
            item.setFulfillStatus(FULFILL_WAIT_CUTOFF);
            item.setAfterSaleStatus(0L);
            item.setExpectedPickupDate(period == null ? null : period.getDeliveryDate());
            orderItemMapper.insert(item);
            lineNo++;
        }
    }

    private void invalidateCarts(final List<OrdCart> carts, final LocalDateTime now) {
        for (final OrdCart cart : carts) {
            cart.setSelectedFlag(0L);
            cart.setValidStatus(CART_INVALID);
            prepareUpdate(cart, now);
            cartMapper.updateById(cart);
        }
    }

    private void closeWaitingPayments(final OrdOrder order, final LocalDateTime now) {
        final List<PayTrade> payTrades = payTradeMapper.selectList(new LambdaQueryWrapper<PayTrade>()
                .eq(PayTrade::getOrderId, order.getId())
                .eq(PayTrade::getTradeStatus, PAY_TRADE_WAIT));
        for (final PayTrade payTrade : payTrades) {
            payTrade.setTradeStatus(PAY_TRADE_CLOSED);
            prepareUpdate(payTrade, now);
            payTradeMapper.updateById(payTrade);
        }
    }

    private BigDecimal calculateCartAmount(final OrdCart cart) {
        validateCartForOrder(cart, LocalDateTime.now());
        final SalePublishSku publishSku = loadPublishSku(cart);
        return publishSku.getSalePrice().multiply(BigDecimal.valueOf(cart.getQty()));
    }

    private Long safeQty(final Long qty) {
        return qty == null ? 0L : qty;
    }

    private void validateCartForOrder(final OrdCart cart, final LocalDateTime now) {
        if (cart.getQty() == null || cart.getQty() <= 0L) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "购物车数量必须大于0");
        }
        final SalePublishPeriod period = periodMapper.selectById(cart.getPeriodId());
        if (period == null || !PERIOD_ONLINE.equals(period.getStatus())) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "团期不存在或未发布");
        }
        if (period.getSaleStartTime() != null && period.getSaleStartTime().isAfter(now)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "团期还未开始销售");
        }
        if (period.getActualCutoffTime() != null && period.getActualCutoffTime().isBefore(now)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "团期已截团");
        }
        final SalePublishSku publishSku = loadPublishSku(cart);
        final Long availableQty = Math.max(0L, safeQty(publishSku.getPlannedStockQty())
                - safeQty(publishSku.getSoldQty()) - safeQty(publishSku.getLockedQty()));
        if (availableQty < cart.getQty()) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "商品可售数量不足");
        }
    }

    private SalePublishSku loadPublishSku(final OrdCart cart) {
        final SalePublishSku publishSku = publishSkuMapper.selectOne(new LambdaQueryWrapper<SalePublishSku>()
                .eq(SalePublishSku::getPeriodId, cart.getPeriodId())
                .eq(SalePublishSku::getProductId, cart.getProductId())
                .eq(SalePublishSku::getSkuId, cart.getSkuId()));
        if (publishSku == null || publishSku.getSalePrice() == null || !PUBLISH_SKU_ENABLED.equals(
                publishSku.getStatus())) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "购物车商品价格已失效");
        }
        return publishSku;
    }

    private void insertStatusLog(
            final Long orderId,
            final Long orderItemId,
            final Long beforeStatus,
            final Long statusLine,
            final Long afterStatus,
            final String reason,
            final Long userId,
            final LocalDateTime now) {
        final OrdOrderStatusLog log = new OrdOrderStatusLog();
        prepareCreate(log, IdWorker.getId(), now);
        log.setOrderId(orderId);
        log.setOrderItemId(orderItemId);
        log.setStatusLine(statusLine);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setChangeReason(reason);
        log.setOperatorType(OPERATOR_USER);
        log.setOperatorId(userId);
        orderStatusLogMapper.insert(log);
    }

    private String buildNo(final String prefix, final LocalDateTime now) {
        return prefix + now.format(NO_TIME_FORMATTER) + IdWorker.getIdStr();
    }

    private List<Long> listUserOrderIds(final Long userId) {
        return orderMapper.selectList(new LambdaQueryWrapper<OrdOrder>()
                        .eq(OrdOrder::getUserId, userId))
                .stream()
                .map(OrdOrder::getId)
                .toList();
    }
}
