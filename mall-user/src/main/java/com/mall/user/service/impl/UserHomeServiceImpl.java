package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.user.dto.UserHomeAssetDTO;
import com.mall.api.user.dto.UserHomeBannerDTO;
import com.mall.api.user.dto.UserHomeCategoryAssetDTO;
import com.mall.api.user.dto.UserHomeDTO;
import com.mall.api.user.dto.UserHomePromoDTO;
import com.mall.api.user.vo.UserHomeAssetQueryVO;
import com.mall.api.user.vo.UserHomeQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.user.entity.MsgRecord;
import com.mall.user.entity.OrdCart;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.OrdOrderItem;
import com.mall.user.entity.PrdCategory;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.entity.SysFileAsset;
import com.mall.user.entity.UsrUser;
import com.mall.user.mapper.MsgRecordMapper;
import com.mall.user.mapper.OrdCartMapper;
import com.mall.user.mapper.OrdOrderItemMapper;
import com.mall.user.mapper.OrdOrderMapper;
import com.mall.user.mapper.PrdCategoryMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
import com.mall.user.mapper.SysFileAssetMapper;
import com.mall.user.mapper.UsrUserMapper;
import com.mall.user.service.UserHomeService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private static final Long CATEGORY_FRONT = 1L;
    private static final Long ENABLED = 1L;
    private static final String BIZ_CATEGORY_ICON = "USER_CATEGORY_ICON";
    private static final String BIZ_HOME_BANNER = "USER_HOME_BANNER";
    private static final String BIZ_HOME_PROMO = "USER_HOME_PROMO";
    private static final String ACTIVITY_PAGE = "/pages/activity/index?activityCode=";

    private final UsrUserMapper userMapper;
    private final PrdCategoryMapper categoryMapper;
    private final SysFileAssetMapper fileAssetMapper;
    private final SalePublishPeriodMapper periodMapper;
    private final SalePublishSkuMapper publishSkuMapper;
    private final OrdCartMapper cartMapper;
    private final OrdOrderMapper orderMapper;
    private final OrdOrderItemMapper orderItemMapper;
    private final MsgRecordMapper msgRecordMapper;

    public UserHomeServiceImpl(
            final UsrUserMapper userMapper,
            final PrdCategoryMapper categoryMapper,
            final SysFileAssetMapper fileAssetMapper,
            final SalePublishPeriodMapper periodMapper,
            final SalePublishSkuMapper publishSkuMapper,
            final OrdCartMapper cartMapper,
            final OrdOrderMapper orderMapper,
            final OrdOrderItemMapper orderItemMapper,
            final MsgRecordMapper msgRecordMapper) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.fileAssetMapper = fileAssetMapper;
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
            final UserHomeDTO result = new UserHomeDTO();
            result.setUserId(query.getUserId());
            result.setCityId(query.getCityId());
            result.setStationId(query.getStationId());
            result.setOnlineProductCount(countOnlineProducts(query.getCityId()));
            result.setCartItemCount(0L);
            result.setWaitPayOrderCount(0L);
            result.setWaitPickupOrderCount(0L);
            result.setUnreadMessageCount(0L);
            return result;
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

    @Override
    public UserHomeAssetDTO getHomeAssets(final UserHomeAssetQueryVO query) {
        final UserHomeAssetDTO result = new UserHomeAssetDTO();
        result.setCategories(buildCategoryAssets());
        result.setBanners(buildBanners());
        result.setPromos(buildPromos());
        return result;
    }

    private List<UserHomeCategoryAssetDTO> buildCategoryAssets() {
        final List<PrdCategory> categories = categoryMapper.selectList(new LambdaQueryWrapper<PrdCategory>()
                .eq(PrdCategory::getCategoryType, CATEGORY_FRONT)
                .eq(PrdCategory::getStatus, ENABLED)
                .orderByAsc(PrdCategory::getSortNo)
                .orderByDesc(PrdCategory::getId));
        final Map<String, SysFileAsset> iconMap = listActiveAssets(BIZ_CATEGORY_ICON).stream()
                .filter(item -> item.getBizNo() != null && item.getFileUrl() != null)
                .collect(Collectors.toMap(SysFileAsset::getBizNo, Function.identity(), (left, right) -> left));
        return categories.stream().map(item -> {
            final UserHomeCategoryAssetDTO asset = new UserHomeCategoryAssetDTO();
            asset.setCategoryId(item.getId());
            asset.setCategoryCode(item.getCategoryCode());
            asset.setCategoryName(item.getCategoryName());
            asset.setSortNo(item.getSortNo());
            asset.setImageUrl(assetUrl(iconMap.get(item.getCategoryCode())));
            return asset;
        }).toList();
    }

    private List<UserHomeBannerDTO> buildBanners() {
        final Map<String, SysFileAsset> bannerMap = listActiveAssets(BIZ_HOME_BANNER).stream()
                .filter(item -> item.getBizNo() != null && item.getFileUrl() != null)
                .collect(Collectors.toMap(SysFileAsset::getBizNo, Function.identity(), (left, right) -> left));
        return List.of(buildBanner("HOME_MAIN", "本周鲜果专场", "基地直采 次日到团点",
                "爆汁橙、蓝莓、梨礼盒限时优惠", bannerMap.get("HOME_MAIN"), 1L))
                .stream()
                .filter(item -> StringUtils.hasText(item.getImageUrl()))
                .toList();
    }

    private UserHomeBannerDTO buildBanner(
            final String code,
            final String title,
            final String subTitle,
            final String description,
            final SysFileAsset asset,
            final Long sortNo) {
        final UserHomeBannerDTO banner = new UserHomeBannerDTO();
        banner.setCode(code);
        banner.setTitle(title);
        banner.setSubTitle(subTitle);
        banner.setDescription(description);
        banner.setImageUrl(assetUrl(asset));
        banner.setActivityCode(code);
        banner.setLinkUrl(ACTIVITY_PAGE + code);
        banner.setSortNo(sortNo);
        return banner;
    }

    private List<UserHomePromoDTO> buildPromos() {
        final Map<String, SysFileAsset> promoMap = listActiveAssets(BIZ_HOME_PROMO).stream()
                .filter(item -> item.getBizNo() != null && item.getFileUrl() != null)
                .collect(Collectors.toMap(SysFileAsset::getBizNo, Function.identity(), (left, right) -> left));
        return List.of(
                buildPromo("ORCHARD_DIRECT", "果园直供", promoMap.get("ORCHARD_DIRECT"), 1L),
                buildPromo("TODAY_NEW", "今日上新", promoMap.get("TODAY_NEW"), 2L),
                buildPromo("BREAKFAST_BAKERY", "早餐烘焙", promoMap.get("BREAKFAST_BAKERY"), 3L))
                .stream()
                .filter(item -> StringUtils.hasText(item.getImageUrl()))
                .toList();
    }

    private UserHomePromoDTO buildPromo(
            final String code,
            final String title,
            final SysFileAsset asset,
            final Long sortNo) {
        final UserHomePromoDTO promo = new UserHomePromoDTO();
        promo.setCode(code);
        promo.setTitle(title);
        promo.setActionText("全部");
        promo.setImageUrl(assetUrl(asset));
        promo.setActivityCode(code);
        promo.setLinkUrl(ACTIVITY_PAGE + code);
        promo.setSortNo(sortNo);
        return promo;
    }

    private List<SysFileAsset> listActiveAssets(final String bizType) {
        return fileAssetMapper.selectList(new LambdaQueryWrapper<SysFileAsset>()
                .eq(SysFileAsset::getBizType, bizType)
                .eq(SysFileAsset::getStatus, ENABLED)
                .orderByDesc(SysFileAsset::getId));
    }

    private String assetUrl(final SysFileAsset asset) {
        return asset == null ? null : asset.getFileUrl();
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
