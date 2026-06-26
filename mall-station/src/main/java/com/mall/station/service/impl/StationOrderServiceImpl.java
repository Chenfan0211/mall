package com.mall.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.station.dto.StationWorkbenchDTO;
import com.mall.api.station.vo.StationArrivalConfirmVO;
import com.mall.api.station.vo.StationDeliveryQueryVO;
import com.mall.api.station.vo.StationOrderQueryVO;
import com.mall.api.station.vo.StationPickupConfirmVO;
import com.mall.api.station.vo.StationWorkbenchQueryVO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.api.wms.dto.ReturnHandoverDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.station.convert.StationConvert;
import com.mall.station.entity.FinAccount;
import com.mall.station.entity.FinCommissionDetail;
import com.mall.station.entity.MsgRecord;
import com.mall.station.entity.OpExceptionRecord;
import com.mall.station.entity.OrdOrder;
import com.mall.station.entity.OrdOrderItem;
import com.mall.station.entity.OrdOrderStatusLog;
import com.mall.station.entity.UsrStationLeader;
import com.mall.station.entity.WmsDeliveryOrder;
import com.mall.station.entity.WmsDeliveryStation;
import com.mall.station.entity.WmsReturnHandover;
import com.mall.station.mapper.FinAccountMapper;
import com.mall.station.mapper.FinCommissionDetailMapper;
import com.mall.station.mapper.MsgRecordMapper;
import com.mall.station.mapper.OpExceptionRecordMapper;
import com.mall.station.mapper.OrdOrderItemMapper;
import com.mall.station.mapper.OrdOrderMapper;
import com.mall.station.mapper.OrdOrderStatusLogMapper;
import com.mall.station.mapper.UsrStationLeaderMapper;
import com.mall.station.mapper.WmsDeliveryOrderMapper;
import com.mall.station.mapper.WmsDeliveryStationMapper;
import com.mall.station.mapper.WmsReturnHandoverMapper;
import com.mall.station.service.StationOrderService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class StationOrderServiceImpl extends StationReadSupport implements StationOrderService {

    private static final Long FULFILL_WAIT_PICKUP = 60L;
    private static final Long FULFILL_PICKED = 70L;
    private static final Long DELIVERY_ARRIVED = 20L;
    private static final Long DELIVERY_COMPLETED = 30L;
    private static final Long DELIVERY_UNABLE = 40L;
    private static final Long TRADE_COMPLETED = 50L;
    private static final Long STATUS_LINE_FULFILL = 3L;
    private static final Long OPERATOR_ROLE_WORKBENCH = 5L;
    private static final Long EXCEPTION_WAIT = 10L;
    private static final Long EXCEPTION_DOING = 20L;
    private static final Long ACCOUNT_SUBJECT_STATION = 2L;
    private static final Long ACCOUNT_SUBJECT_LEADER = 3L;
    private static final Long COMMISSION_SUBJECT_STATION = 1L;
    private static final Long COMMISSION_SUBJECT_LEADER = 2L;
    private static final Long MSG_RECEIVER_STATION = 4L;
    private static final Long MSG_RECEIVER_LEADER = 5L;
    private static final Long MSG_UNREAD = 0L;
    private static final Long COMMISSION_WAIT_CALC = 10L;
    private static final Long COMMISSION_WAIT_SPLIT = 20L;

    private final OrdOrderMapper orderMapper;
    private final OrdOrderItemMapper orderItemMapper;
    private final WmsDeliveryOrderMapper deliveryOrderMapper;
    private final WmsDeliveryStationMapper deliveryStationMapper;
    private final WmsReturnHandoverMapper returnHandoverMapper;
    private final OpExceptionRecordMapper exceptionRecordMapper;
    private final FinAccountMapper accountMapper;
    private final FinCommissionDetailMapper commissionDetailMapper;
    private final MsgRecordMapper msgRecordMapper;
    private final UsrStationLeaderMapper stationLeaderMapper;
    private final OrdOrderStatusLogMapper orderStatusLogMapper;
    private final StationConvert stationConvert;

    public StationOrderServiceImpl(
            final OrdOrderMapper orderMapper,
            final OrdOrderItemMapper orderItemMapper,
            final WmsDeliveryOrderMapper deliveryOrderMapper,
            final WmsDeliveryStationMapper deliveryStationMapper,
            final WmsReturnHandoverMapper returnHandoverMapper,
            final OpExceptionRecordMapper exceptionRecordMapper,
            final FinAccountMapper accountMapper,
            final FinCommissionDetailMapper commissionDetailMapper,
            final MsgRecordMapper msgRecordMapper,
            final UsrStationLeaderMapper stationLeaderMapper,
            final OrdOrderStatusLogMapper orderStatusLogMapper,
            final StationConvert stationConvert) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.deliveryOrderMapper = deliveryOrderMapper;
        this.deliveryStationMapper = deliveryStationMapper;
        this.returnHandoverMapper = returnHandoverMapper;
        this.exceptionRecordMapper = exceptionRecordMapper;
        this.accountMapper = accountMapper;
        this.commissionDetailMapper = commissionDetailMapper;
        this.msgRecordMapper = msgRecordMapper;
        this.stationLeaderMapper = stationLeaderMapper;
        this.orderStatusLogMapper = orderStatusLogMapper;
        this.stationConvert = stationConvert;
    }

    @Override
    public StationWorkbenchDTO getWorkbench(final StationWorkbenchQueryVO query) {
        final StationWorkbenchQueryVO safeQuery = query == null ? new StationWorkbenchQueryVO() : query;
        final LocalDate bizDate = safeQuery.getBizDate() == null ? LocalDate.now() : safeQuery.getBizDate();
        final StationWorkbenchDTO result = new StationWorkbenchDTO();
        result.setStationId(safeQuery.getStationId());
        result.setLeaderId(safeQuery.getLeaderId());
        result.setTodayOrderCount(countOrderItems(safeQuery, bizDate, null));
        result.setWaitPickupCount(countOrderItems(safeQuery, bizDate, FULFILL_WAIT_PICKUP));
        result.setPickedCount(countOrderItems(safeQuery, bizDate, FULFILL_PICKED));
        result.setArrivalCount(countArrivals(safeQuery, bizDate));
        result.setExceptionCount(countExceptions(safeQuery));
        result.setUnreadMessageCount(countUnreadMessages(safeQuery));
        appendAccountSummary(result, safeQuery);
        result.setPendingCommissionAmount(sumPendingCommission(safeQuery));
        return result;
    }

    @Override
    public PageResult<OrderDTO> pageOrders(final StationOrderQueryVO query) {
        final StationOrderQueryVO safeQuery = query == null ? new StationOrderQueryVO() : query;
        final LambdaQueryWrapper<OrdOrder> wrapper = new LambdaQueryWrapper<OrdOrder>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(OrdOrder::getOrderNo, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrder::getPickupName, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrder::getPickupMobile, safeQuery.getKeyword()))
                .eq(safeQuery.getStationId() != null, OrdOrder::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getLeaderId() != null, OrdOrder::getLeaderId, safeQuery.getLeaderId())
                .eq(safeQuery.getTradeStatus() != null, OrdOrder::getTradeStatus, safeQuery.getTradeStatus())
                .eq(safeQuery.getPayStatus() != null, OrdOrder::getPayStatus, safeQuery.getPayStatus())
                .eq(safeQuery.getFulfillStatus() != null, OrdOrder::getFulfillStatus, safeQuery.getFulfillStatus())
                .orderByDesc(OrdOrder::getId);
        return toPage(safeQuery, orderMapper, wrapper, stationConvert::toOrderDTO);
    }

    @Override
    public OrderDTO getOrder(final Long id) {
        return getById(id, orderMapper, stationConvert::toOrderDTO, "订单");
    }

    @Override
    public PageResult<OrderItemDTO> pageOrderItems(final StationOrderQueryVO query) {
        final StationOrderQueryVO safeQuery = query == null ? new StationOrderQueryVO() : query;
        final LambdaQueryWrapper<OrdOrderItem> wrapper = new LambdaQueryWrapper<OrdOrderItem>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(OrdOrderItem::getOrderNo, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrderItem::getProductName, safeQuery.getKeyword())
                        .or()
                        .like(OrdOrderItem::getSkuName, safeQuery.getKeyword()))
                .eq(safeQuery.getStationId() != null, OrdOrderItem::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getFulfillStatus() != null, OrdOrderItem::getFulfillStatus,
                        safeQuery.getFulfillStatus())
                .eq(safeQuery.getExpectedPickupDate() != null, OrdOrderItem::getExpectedPickupDate,
                        safeQuery.getExpectedPickupDate())
                .orderByDesc(OrdOrderItem::getId);
        return toPage(safeQuery, orderItemMapper, wrapper, stationConvert::toOrderItemDTO);
    }

    @Override
    public OrderItemDTO getOrderItem(final Long id) {
        return getById(id, orderItemMapper, stationConvert::toOrderItemDTO, "订单商品");
    }

    @Override
    @Transactional
    public OrderItemDTO confirmPickup(final Long id, final StationPickupConfirmVO request) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "订单商品ID不能为空");
        }
        final OrdOrderItem item = orderItemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "订单商品不存在");
        }
        if (Objects.equals(item.getFulfillStatus(), FULFILL_PICKED)) {
            return stationConvert.toOrderItemDTO(item);
        }
        if (!Objects.equals(item.getFulfillStatus(), FULFILL_WAIT_PICKUP)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "只有待自提订单商品才能核销");
        }
        final Long maxPickableQty = safeQty(item.getQty()) - safeQty(item.getRefundedQty());
        final Long beforePickedQty = safeQty(item.getPickedQty());
        final Long requestPickedQty = resolvePickupQty(request, maxPickableQty, beforePickedQty);
        final Long afterPickedQty = beforePickedQty + requestPickedQty;
        if (afterPickedQty > maxPickableQty) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "核销数量不能超过可提货数量");
        }
        final LocalDateTime now = LocalDateTime.now();
        final Long beforeStatus = item.getFulfillStatus();
        item.setPickedQty(afterPickedQty);
        if (afterPickedQty.equals(maxPickableQty)) {
            item.setFulfillStatus(FULFILL_PICKED);
        }
        prepareUpdate(item, now);
        orderItemMapper.updateById(item);
        if (!Objects.equals(beforeStatus, item.getFulfillStatus())) {
            insertStatusLog(item, beforeStatus, request == null ? null : request.getOperatorId(),
                    request == null ? null : request.getRemark(), now);
            syncOrderPickedIfComplete(item.getOrderId(), now, request == null ? null : request.getOperatorId());
        }
        return stationConvert.toOrderItemDTO(item);
    }

    @Override
    public PageResult<DeliveryOrderDTO> pageDeliveryOrders(final StationDeliveryQueryVO query) {
        final StationDeliveryQueryVO safeQuery = query == null ? new StationDeliveryQueryVO() : query;
        final List<Long> deliveryIds = listDeliveryIdsByStation(safeQuery.getStationId());
        final LambdaQueryWrapper<WmsDeliveryOrder> wrapper = new LambdaQueryWrapper<WmsDeliveryOrder>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsDeliveryOrder::getDeliveryNo, safeQuery.getKeyword()))
                .eq(safeQuery.getDeliveryId() != null, WmsDeliveryOrder::getId, safeQuery.getDeliveryId())
                .in(!CollectionUtils.isEmpty(deliveryIds), WmsDeliveryOrder::getId, deliveryIds)
                .eq(safeQuery.getStationId() != null && deliveryIds.isEmpty(), WmsDeliveryOrder::getId, -1L)
                .eq(safeQuery.getStatus() != null, WmsDeliveryOrder::getStatus, safeQuery.getStatus())
                .eq(safeQuery.getDeliveryDate() != null, WmsDeliveryOrder::getDeliveryDate,
                        safeQuery.getDeliveryDate())
                .orderByDesc(WmsDeliveryOrder::getId);
        return toPage(safeQuery, deliveryOrderMapper, wrapper, stationConvert::toDeliveryOrderDTO);
    }

    @Override
    public DeliveryOrderDTO getDeliveryOrder(final Long id) {
        return getById(id, deliveryOrderMapper, stationConvert::toDeliveryOrderDTO, "配送单");
    }

    @Override
    public PageResult<DeliveryStationDTO> pageDeliveryStations(final StationDeliveryQueryVO query) {
        final StationDeliveryQueryVO safeQuery = query == null ? new StationDeliveryQueryVO() : query;
        final LambdaQueryWrapper<WmsDeliveryStation> wrapper = new LambdaQueryWrapper<WmsDeliveryStation>()
                .eq(safeQuery.getStationId() != null, WmsDeliveryStation::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getDeliveryId() != null, WmsDeliveryStation::getDeliveryId, safeQuery.getDeliveryId())
                .eq(safeQuery.getStatus() != null, WmsDeliveryStation::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsDeliveryStation::getId);
        return toPage(safeQuery, deliveryStationMapper, wrapper, stationConvert::toDeliveryStationDTO);
    }

    @Override
    public DeliveryStationDTO getDeliveryStation(final Long id) {
        return getById(id, deliveryStationMapper, stationConvert::toDeliveryStationDTO, "配送自提点");
    }

    @Override
    @Transactional
    public DeliveryStationDTO confirmArrival(final Long id, final StationArrivalConfirmVO request) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "配送自提点ID不能为空");
        }
        final WmsDeliveryStation station = deliveryStationMapper.selectById(id);
        if (station == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "配送自提点不存在");
        }
        if (Objects.equals(station.getStatus(), DELIVERY_COMPLETED)) {
            return stationConvert.toDeliveryStationDTO(station);
        }
        if (!Objects.equals(station.getStatus(), DELIVERY_ARRIVED)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "只有已到达自提点才能确认交接完成");
        }
        final LocalDateTime now = LocalDateTime.now();
        station.setStatus(DELIVERY_COMPLETED);
        station.setCompleteTime(now);
        if (request != null && StringUtils.hasText(request.getRemark())) {
            station.setRemark(request.getRemark());
        }
        prepareUpdate(station, now);
        deliveryStationMapper.updateById(station);
        syncDeliveryOrderCompleteIfAllStationsDone(station.getDeliveryId(), now);
        return stationConvert.toDeliveryStationDTO(station);
    }

    @Override
    public PageResult<ReturnHandoverDTO> pageReturnHandovers(final StationDeliveryQueryVO query) {
        final StationDeliveryQueryVO safeQuery = query == null ? new StationDeliveryQueryVO() : query;
        final LambdaQueryWrapper<WmsReturnHandover> wrapper = new LambdaQueryWrapper<WmsReturnHandover>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsReturnHandover::getReturnNo, safeQuery.getKeyword()))
                .eq(safeQuery.getStationId() != null, WmsReturnHandover::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getDeliveryId() != null, WmsReturnHandover::getDeliveryId, safeQuery.getDeliveryId())
                .eq(safeQuery.getStatus() != null, WmsReturnHandover::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsReturnHandover::getId);
        return toPage(safeQuery, returnHandoverMapper, wrapper, stationConvert::toReturnHandoverDTO);
    }

    @Override
    public ReturnHandoverDTO getReturnHandover(final Long id) {
        return getById(id, returnHandoverMapper, stationConvert::toReturnHandoverDTO, "回仓交接");
    }

    @Override
    public PageResult<ExceptionRecordDTO> pageExceptions(final StationDeliveryQueryVO query) {
        final StationDeliveryQueryVO safeQuery = query == null ? new StationDeliveryQueryVO() : query;
        final LambdaQueryWrapper<OpExceptionRecord> wrapper = new LambdaQueryWrapper<OpExceptionRecord>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(OpExceptionRecord::getExceptionNo, safeQuery.getKeyword())
                        .or()
                        .like(OpExceptionRecord::getTitle, safeQuery.getKeyword())
                        .or()
                        .like(OpExceptionRecord::getDescription, safeQuery.getKeyword()))
                .eq(safeQuery.getStationId() != null, OpExceptionRecord::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getStatus() != null, OpExceptionRecord::getStatus, safeQuery.getStatus())
                .orderByDesc(OpExceptionRecord::getId);
        return toPage(safeQuery, exceptionRecordMapper, wrapper, stationConvert::toExceptionRecordDTO);
    }

    private Long countOrderItems(
            final StationWorkbenchQueryVO query,
            final LocalDate expectedPickupDate,
            final Long fulfillStatus) {
        final List<Long> stationIds = listStationIdsByWorkbench(query);
        final LambdaQueryWrapper<OrdOrderItem> wrapper = new LambdaQueryWrapper<OrdOrderItem>()
                .eq(query.getStationId() != null, OrdOrderItem::getStationId, query.getStationId())
                .in(query.getStationId() == null && !stationIds.isEmpty(), OrdOrderItem::getStationId, stationIds)
                .eq(query.getStationId() == null && query.getLeaderId() != null && stationIds.isEmpty(),
                        OrdOrderItem::getId, -1L)
                .eq(expectedPickupDate != null, OrdOrderItem::getExpectedPickupDate, expectedPickupDate)
                .eq(fulfillStatus != null, OrdOrderItem::getFulfillStatus, fulfillStatus);
        return orderItemMapper.selectCount(wrapper);
    }

    private Long countArrivals(final StationWorkbenchQueryVO query, final LocalDate deliveryDate) {
        final List<Long> stationIds = listStationIdsByWorkbench(query);
        final List<Long> deliveryIds = deliveryOrderMapper.selectList(new LambdaQueryWrapper<WmsDeliveryOrder>()
                        .eq(WmsDeliveryOrder::getDeliveryDate, deliveryDate))
                .stream()
                .map(WmsDeliveryOrder::getId)
                .toList();
        if (deliveryIds.isEmpty()) {
            return 0L;
        }
        final LambdaQueryWrapper<WmsDeliveryStation> wrapper = new LambdaQueryWrapper<WmsDeliveryStation>()
                .in(WmsDeliveryStation::getDeliveryId, deliveryIds)
                .eq(query.getStationId() != null, WmsDeliveryStation::getStationId, query.getStationId())
                .in(query.getStationId() == null && !stationIds.isEmpty(), WmsDeliveryStation::getStationId,
                        stationIds)
                .eq(query.getStationId() == null && query.getLeaderId() != null && stationIds.isEmpty(),
                        WmsDeliveryStation::getId, -1L)
                .in(WmsDeliveryStation::getStatus, List.of(DELIVERY_ARRIVED, DELIVERY_COMPLETED));
        return deliveryStationMapper.selectCount(wrapper);
    }

    private Long countExceptions(final StationWorkbenchQueryVO query) {
        final List<Long> stationIds = listStationIdsByWorkbench(query);
        final LambdaQueryWrapper<OpExceptionRecord> wrapper = new LambdaQueryWrapper<OpExceptionRecord>()
                .eq(query.getStationId() != null, OpExceptionRecord::getStationId, query.getStationId())
                .in(query.getStationId() == null && !stationIds.isEmpty(), OpExceptionRecord::getStationId,
                        stationIds)
                .eq(query.getStationId() == null && query.getLeaderId() != null && stationIds.isEmpty(),
                        OpExceptionRecord::getId, -1L)
                .in(OpExceptionRecord::getStatus, List.of(EXCEPTION_WAIT, EXCEPTION_DOING));
        return exceptionRecordMapper.selectCount(wrapper);
    }

    private Long countUnreadMessages(final StationWorkbenchQueryVO query) {
        final LambdaQueryWrapper<MsgRecord> wrapper = new LambdaQueryWrapper<MsgRecord>()
                .eq(MsgRecord::getReadStatus, MSG_UNREAD);
        if (query.getStationId() != null) {
            wrapper.eq(MsgRecord::getReceiverType, MSG_RECEIVER_STATION)
                    .eq(MsgRecord::getReceiverId, query.getStationId());
        } else if (query.getLeaderId() != null) {
            wrapper.eq(MsgRecord::getReceiverType, MSG_RECEIVER_LEADER)
                    .eq(MsgRecord::getReceiverId, query.getLeaderId());
        }
        return msgRecordMapper.selectCount(wrapper);
    }

    private void appendAccountSummary(final StationWorkbenchDTO result, final StationWorkbenchQueryVO query) {
        final LambdaQueryWrapper<FinAccount> wrapper = new LambdaQueryWrapper<FinAccount>();
        if (query.getStationId() != null) {
            wrapper.eq(FinAccount::getSubjectType, ACCOUNT_SUBJECT_STATION)
                    .eq(FinAccount::getSubjectId, query.getStationId());
        } else if (query.getLeaderId() != null) {
            wrapper.eq(FinAccount::getSubjectType, ACCOUNT_SUBJECT_LEADER)
                    .eq(FinAccount::getSubjectId, query.getLeaderId());
        }
        wrapper.orderByAsc(FinAccount::getId);
        final List<FinAccount> accounts = accountMapper.selectList(wrapper);
        if (accounts.isEmpty()) {
            result.setAvailableAmount(BigDecimal.ZERO);
            result.setFrozenAmount(BigDecimal.ZERO);
            return;
        }
        final FinAccount account = accounts.get(0);
        result.setAvailableAmount(
                account.getAvailableAmount() == null ? BigDecimal.ZERO : account.getAvailableAmount());
        result.setFrozenAmount(account.getFrozenAmount() == null ? BigDecimal.ZERO : account.getFrozenAmount());
    }

    private BigDecimal sumPendingCommission(final StationWorkbenchQueryVO query) {
        final LambdaQueryWrapper<FinCommissionDetail> wrapper = new LambdaQueryWrapper<FinCommissionDetail>();
        if (query.getStationId() != null) {
            wrapper.eq(FinCommissionDetail::getSubjectType, COMMISSION_SUBJECT_STATION)
                    .eq(FinCommissionDetail::getSubjectId, query.getStationId());
        } else if (query.getLeaderId() != null) {
            wrapper.eq(FinCommissionDetail::getSubjectType, COMMISSION_SUBJECT_LEADER)
                    .eq(FinCommissionDetail::getSubjectId, query.getLeaderId());
        }
        wrapper.in(FinCommissionDetail::getStatus, List.of(COMMISSION_WAIT_CALC, COMMISSION_WAIT_SPLIT));
        return commissionDetailMapper.selectList(wrapper).stream()
                .map(FinCommissionDetail::getCommissionAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Long> listDeliveryIdsByStation(final Long stationId) {
        if (stationId == null) {
            return List.of();
        }
        return deliveryStationMapper.selectList(new LambdaQueryWrapper<WmsDeliveryStation>()
                        .eq(WmsDeliveryStation::getStationId, stationId))
                .stream()
                .map(WmsDeliveryStation::getDeliveryId)
                .toList();
    }

    private List<Long> listStationIdsByWorkbench(final StationWorkbenchQueryVO query) {
        if (query.getStationId() != null || query.getLeaderId() == null) {
            return List.of();
        }
        return stationLeaderMapper.selectList(new LambdaQueryWrapper<UsrStationLeader>()
                        .eq(UsrStationLeader::getLeaderId, query.getLeaderId()))
                .stream()
                .map(UsrStationLeader::getStationId)
                .toList();
    }

    private Long safeQty(final Long qty) {
        return qty == null ? 0L : qty;
    }

    private Long resolvePickupQty(
            final StationPickupConfirmVO request,
            final Long maxPickableQty,
            final Long beforePickedQty) {
        final Long remainQty = maxPickableQty - beforePickedQty;
        if (remainQty <= 0L) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "订单商品已无可核销数量");
        }
        if (request == null || request.getPickedQty() == null) {
            return remainQty;
        }
        if (request.getPickedQty() <= 0L) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "核销数量必须大于0");
        }
        return request.getPickedQty();
    }

    private void insertStatusLog(
            final OrdOrderItem item,
            final Long beforeStatus,
            final Long operatorId,
            final String remark,
            final LocalDateTime now) {
        final OrdOrderStatusLog log = new OrdOrderStatusLog();
        prepareCreate(log, IdWorker.getId(), now);
        log.setOrderId(item.getOrderId());
        log.setOrderItemId(item.getId());
        log.setStatusLine(STATUS_LINE_FULFILL);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(item.getFulfillStatus());
        log.setChangeReason(StringUtils.hasText(remark) ? remark : "角色工作台自提核销");
        log.setOperatorType(OPERATOR_ROLE_WORKBENCH);
        log.setOperatorId(operatorId);
        orderStatusLogMapper.insert(log);
    }

    private void syncOrderPickedIfComplete(final Long orderId, final LocalDateTime now, final Long operatorId) {
        if (orderId == null) {
            return;
        }
        final List<OrdOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrdOrderItem>()
                .eq(OrdOrderItem::getOrderId, orderId));
        if (items.isEmpty() || items.stream().anyMatch(item -> !Objects.equals(item.getFulfillStatus(),
                FULFILL_PICKED))) {
            return;
        }
        final OrdOrder order = orderMapper.selectById(orderId);
        if (order == null || Objects.equals(order.getFulfillStatus(), FULFILL_PICKED)) {
            return;
        }
        final Long beforeStatus = order.getFulfillStatus();
        order.setFulfillStatus(FULFILL_PICKED);
        order.setTradeStatus(TRADE_COMPLETED);
        order.setCompleteTime(now);
        prepareUpdate(order, now);
        orderMapper.updateById(order);
        insertOrderStatusLog(order, beforeStatus, operatorId, now);
    }

    private void insertOrderStatusLog(
            final OrdOrder order,
            final Long beforeStatus,
            final Long operatorId,
            final LocalDateTime now) {
        final OrdOrderStatusLog log = new OrdOrderStatusLog();
        prepareCreate(log, IdWorker.getId(), now);
        log.setOrderId(order.getId());
        log.setOrderItemId(null);
        log.setStatusLine(STATUS_LINE_FULFILL);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(order.getFulfillStatus());
        log.setChangeReason("角色工作台订单全部核销完成");
        log.setOperatorType(OPERATOR_ROLE_WORKBENCH);
        log.setOperatorId(operatorId);
        orderStatusLogMapper.insert(log);
    }

    private void syncDeliveryOrderCompleteIfAllStationsDone(final Long deliveryId, final LocalDateTime now) {
        if (deliveryId == null) {
            return;
        }
        final List<WmsDeliveryStation> stations = deliveryStationMapper.selectList(
                new LambdaQueryWrapper<WmsDeliveryStation>().eq(WmsDeliveryStation::getDeliveryId, deliveryId));
        if (stations.isEmpty() || stations.stream().anyMatch(station -> !Objects.equals(station.getStatus(),
                DELIVERY_COMPLETED) && !Objects.equals(station.getStatus(), DELIVERY_UNABLE))) {
            return;
        }
        final WmsDeliveryOrder order = deliveryOrderMapper.selectById(deliveryId);
        if (order == null || Objects.equals(order.getStatus(), DELIVERY_COMPLETED)) {
            return;
        }
        order.setStatus(DELIVERY_COMPLETED);
        order.setCompleteTime(now);
        prepareUpdate(order, now);
        deliveryOrderMapper.updateById(order);
    }
}
