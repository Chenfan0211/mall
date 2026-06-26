package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.user.dto.UserHomeDTO;
import com.mall.api.user.vo.UserHomeQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.user.entity.MsgRecord;
import com.mall.user.entity.OrdCart;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.OrdOrderItem;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.entity.UsrUser;
import com.mall.user.mapper.MsgRecordMapper;
import com.mall.user.mapper.OrdCartMapper;
import com.mall.user.mapper.OrdOrderItemMapper;
import com.mall.user.mapper.OrdOrderMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
import com.mall.user.mapper.UsrUserMapper;
import com.mall.user.service.UserHomeService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserHomeServiceImpl implements UserHomeService {

    private static final Long USER_NORMAL = 1L;
    private static final Long PERIOD_ONLINE = 20L;
    private static final Long PUBLISH_SKU_ENABLE = 1L;
    private static final Long CART_VALID = 1L;
    private static final Long TRADE_WAIT_PAY = 10L;
    private static final Long FULFILL_WAIT_PICKUP = 60L;
    private static final Long MSG_RECEIVER_USER = 1L;
    private static final Long MSG_UNREAD = 0L;

    private final UsrUserMapper userMapper;
    private final SalePublishPeriodMapper periodMapper;
    private final SalePublishSkuMapper publishSkuMapper;
    private final OrdCartMapper cartMapper;
    private final OrdOrderMapper orderMapper;
    private final OrdOrderItemMapper orderItemMapper;
    private final MsgRecordMapper msgRecordMapper;

    public UserHomeServiceImpl(
            final UsrUserMapper userMapper,
            final SalePublishPeriodMapper periodMapper,
            final SalePublishSkuMapper publishSkuMapper,
            final OrdCartMapper cartMapper,
            final OrdOrderMapper orderMapper,
            final OrdOrderItemMapper orderItemMapper,
            final MsgRecordMapper msgRecordMapper) {
        this.userMapper = userMapper;
        this.periodMapper = periodMapper;
        this.publishSkuMapper = publishSkuMapper;
        this.cartMapper = cartMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.msgRecordMapper = msgRecordMapper;
    }

    @Override
    public UserHomeDTO getHome(final UserHomeQueryVO query) {
        if (query == null || query.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        final UsrUser user = userMapper.selectById(query.getUserId());
        if (user == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "用户不存在");
        }
        if (!USER_NORMAL.equals(user.getStatus())) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "用户状态不可用");
        }
        final Long cityId = query.getCityId() == null ? user.getDefaultCityId() : query.getCityId();
        final Long stationId = query.getStationId() == null ? user.getDefaultStationId() : query.getStationId();
        final UserHomeDTO result = new UserHomeDTO();
        result.setUserId(user.getId());
        result.setCityId(cityId);
        result.setStationId(stationId);
        result.setOnlineProductCount(countOnlineProducts(cityId));
        result.setCartItemCount(countCartItems(user.getId(), cityId, stationId));
        result.setWaitPayOrderCount(countWaitPayOrders(user.getId()));
        result.setWaitPickupOrderCount(countWaitPickupItems(user.getId()));
        result.setUnreadMessageCount(countUnreadMessages(user.getId()));
        return result;
    }

    private Long countOnlineProducts(final Long cityId) {
        final List<Long> periodIds = periodMapper.selectList(new LambdaQueryWrapper<SalePublishPeriod>()
                        .eq(cityId != null, SalePublishPeriod::getCityId, cityId)
                        .eq(SalePublishPeriod::getStatus, PERIOD_ONLINE)
                        .le(SalePublishPeriod::getSaleStartTime, LocalDateTime.now())
                        .ge(SalePublishPeriod::getActualCutoffTime, LocalDateTime.now()))
                .stream()
                .map(SalePublishPeriod::getId)
                .toList();
        if (periodIds.isEmpty()) {
            return 0L;
        }
        return publishSkuMapper.selectCount(new LambdaQueryWrapper<SalePublishSku>()
                .in(SalePublishSku::getPeriodId, periodIds)
                .eq(SalePublishSku::getStatus, PUBLISH_SKU_ENABLE));
    }

    private Long countCartItems(final Long userId, final Long cityId, final Long stationId) {
        return cartMapper.selectCount(new LambdaQueryWrapper<OrdCart>()
                .eq(OrdCart::getUserId, userId)
                .eq(cityId != null, OrdCart::getCityId, cityId)
                .eq(stationId != null, OrdCart::getStationId, stationId)
                .eq(OrdCart::getValidStatus, CART_VALID));
    }

    private Long countWaitPayOrders(final Long userId) {
        return orderMapper.selectCount(new LambdaQueryWrapper<OrdOrder>()
                .eq(OrdOrder::getUserId, userId)
                .eq(OrdOrder::getTradeStatus, TRADE_WAIT_PAY));
    }

    private Long countWaitPickupItems(final Long userId) {
        final List<Long> orderIds = orderMapper.selectList(new LambdaQueryWrapper<OrdOrder>()
                        .eq(OrdOrder::getUserId, userId))
                .stream()
                .map(OrdOrder::getId)
                .toList();
        if (orderIds.isEmpty()) {
            return 0L;
        }
        return orderItemMapper.selectCount(new LambdaQueryWrapper<OrdOrderItem>()
                .in(OrdOrderItem::getOrderId, orderIds)
                .eq(OrdOrderItem::getFulfillStatus, FULFILL_WAIT_PICKUP));
    }

    private Long countUnreadMessages(final Long userId) {
        return msgRecordMapper.selectCount(new LambdaQueryWrapper<MsgRecord>()
                .eq(MsgRecord::getReceiverType, MSG_RECEIVER_USER)
                .eq(MsgRecord::getReceiverId, userId)
                .eq(MsgRecord::getReadStatus, MSG_UNREAD));
    }
}
