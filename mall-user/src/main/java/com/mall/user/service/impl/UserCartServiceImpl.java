package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.user.dto.UserCartItemDTO;
import com.mall.api.user.vo.UserCartAddVO;
import com.mall.api.user.vo.UserCartQueryVO;
import com.mall.api.user.vo.UserCartUpdateVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.OrdCart;
import com.mall.user.entity.PrdProduct;
import com.mall.user.entity.PrdSku;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.mapper.OrdCartMapper;
import com.mall.user.mapper.PrdProductMapper;
import com.mall.user.mapper.PrdSkuMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
import com.mall.user.service.UserCartService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserCartServiceImpl extends UserReadSupport implements UserCartService {

    private static final Long ENABLED = 1L;
    private static final Long PERIOD_ONLINE = 20L;
    private static final Long CART_SELECTED = 1L;
    private static final Long CART_NOT_SELECTED = 0L;
    private static final Long CART_VALID = 1L;
    private static final Long CART_INVALID = 2L;

    private final OrdCartMapper cartMapper;
    private final SalePublishPeriodMapper periodMapper;
    private final SalePublishSkuMapper publishSkuMapper;
    private final PrdProductMapper productMapper;
    private final PrdSkuMapper skuMapper;
    private final UserConvert userConvert;

    public UserCartServiceImpl(
            final OrdCartMapper cartMapper,
            final SalePublishPeriodMapper periodMapper,
            final SalePublishSkuMapper publishSkuMapper,
            final PrdProductMapper productMapper,
            final PrdSkuMapper skuMapper,
            final UserConvert userConvert) {
        this.cartMapper = cartMapper;
        this.periodMapper = periodMapper;
        this.publishSkuMapper = publishSkuMapper;
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.userConvert = userConvert;
    }

    @Override
    public PageResult<UserCartItemDTO> pageCartItems(final UserCartQueryVO query) {
        final UserCartQueryVO safeQuery = query == null ? new UserCartQueryVO() : query;
        if (safeQuery.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        final LambdaQueryWrapper<OrdCart> wrapper = new LambdaQueryWrapper<OrdCart>()
                .eq(OrdCart::getUserId, safeQuery.getUserId())
                .eq(safeQuery.getCityId() != null, OrdCart::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStationId() != null, OrdCart::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getSelectedFlag() != null, OrdCart::getSelectedFlag, safeQuery.getSelectedFlag())
                .eq(safeQuery.getValidStatus() != null, OrdCart::getValidStatus, safeQuery.getValidStatus())
                .orderByDesc(OrdCart::getId);
        final Page<OrdCart> page = cartMapper.selectPage(Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()),
                wrapper);
        if (page.getRecords().isEmpty()) {
            return PageResult.of(page.getTotal(), Collections.emptyList());
        }
        return PageResult.of(page.getTotal(), enrichCartItems(page.getRecords()));
    }

    @Override
    @Transactional
    public UserCartItemDTO addCartItem(final UserCartAddVO request) {
        if (request == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "购物车商品不能为空");
        }
        validatePublishSku(request);
        final LocalDateTime now = LocalDateTime.now();
        final OrdCart existing = cartMapper.selectOne(new LambdaQueryWrapper<OrdCart>()
                .eq(OrdCart::getUserId, request.getUserId())
                .eq(OrdCart::getStationId, request.getStationId())
                .eq(OrdCart::getPeriodId, request.getPeriodId())
                .eq(OrdCart::getSkuId, request.getSkuId()));
        if (existing != null) {
            existing.setQty(zeroIfNull(existing.getQty()) + request.getQty());
            existing.setSelectedFlag(CART_SELECTED);
            existing.setValidStatus(CART_VALID);
            prepareUpdate(existing, now);
            cartMapper.updateById(existing);
            return enrichCartItem(existing);
        }
        final OrdCart cart = new OrdCart();
        prepareCreate(cart, IdWorker.getId(), now);
        cart.setUserId(request.getUserId());
        cart.setCityId(request.getCityId());
        cart.setStationId(request.getStationId());
        cart.setPeriodId(request.getPeriodId());
        cart.setProductId(request.getProductId());
        cart.setSkuId(request.getSkuId());
        cart.setQty(request.getQty());
        cart.setSelectedFlag(CART_SELECTED);
        cart.setValidStatus(CART_VALID);
        cartMapper.insert(cart);
        return enrichCartItem(cart);
    }

    @Override
    @Transactional
    public UserCartItemDTO updateCartItem(final Long id, final UserCartUpdateVO request) {
        final OrdCart cart = loadUserCart(id, request);
        final LocalDateTime now = LocalDateTime.now();
        if (request.getQty() != null) {
            cart.setQty(request.getQty());
        }
        if (request.getSelectedFlag() != null) {
            if (!Objects.equals(request.getSelectedFlag(), CART_SELECTED)
                    && !Objects.equals(request.getSelectedFlag(), CART_NOT_SELECTED)) {
                throw new BusinessException(CommonErrorCode.PARAM_ERROR, "选中状态只能为0或1");
            }
            cart.setSelectedFlag(request.getSelectedFlag());
        }
        prepareUpdate(cart, now);
        cartMapper.updateById(cart);
        return enrichCartItem(cart);
    }

    @Override
    @Transactional
    public UserCartItemDTO removeCartItem(final Long id, final UserCartUpdateVO request) {
        final OrdCart cart = loadUserCart(id, request);
        final LocalDateTime now = LocalDateTime.now();
        cart.setSelectedFlag(CART_NOT_SELECTED);
        cart.setValidStatus(CART_INVALID);
        prepareUpdate(cart, now);
        cartMapper.updateById(cart);
        return enrichCartItem(cart);
    }

    private void validatePublishSku(final UserCartAddVO request) {
        final SalePublishPeriod period = periodMapper.selectById(request.getPeriodId());
        if (period == null || !PERIOD_ONLINE.equals(period.getStatus())) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "团期不存在或未发布");
        }
        final LocalDateTime now = LocalDateTime.now();
        if (period.getSaleStartTime() != null && period.getSaleStartTime().isAfter(now)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "团期还未开始销售");
        }
        if (period.getActualCutoffTime() != null && period.getActualCutoffTime().isBefore(now)) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "团期已截团");
        }
        final SalePublishSku publishSku = publishSkuMapper.selectOne(new LambdaQueryWrapper<SalePublishSku>()
                .eq(SalePublishSku::getPeriodId, request.getPeriodId())
                .eq(SalePublishSku::getProductId, request.getProductId())
                .eq(SalePublishSku::getSkuId, request.getSkuId())
                .eq(SalePublishSku::getStatus, ENABLED));
        if (publishSku == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "团期商品不存在或已下架");
        }
        final Long availableQty = Math.max(0L, zeroIfNull(publishSku.getPlannedStockQty())
                - zeroIfNull(publishSku.getSoldQty()) - zeroIfNull(publishSku.getLockedQty()));
        if (availableQty < request.getQty()) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "商品可售数量不足");
        }
    }

    private OrdCart loadUserCart(final Long id, final UserCartUpdateVO request) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "购物车ID不能为空");
        }
        if (request == null || request.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        final OrdCart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "购物车商品不存在");
        }
        if (!request.getUserId().equals(cart.getUserId())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN, "不能操作其他用户购物车");
        }
        return cart;
    }

    private UserCartItemDTO enrichCartItem(final OrdCart cart) {
        return enrichCartItems(java.util.List.of(cart)).get(0);
    }

    private java.util.List<UserCartItemDTO> enrichCartItems(final java.util.List<OrdCart> carts) {
        final Map<Long, PrdProduct> productMap = productMapper.selectBatchIds(carts.stream()
                        .map(OrdCart::getProductId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(PrdProduct::getId, Function.identity()));
        final Map<Long, PrdSku> skuMap = skuMapper.selectBatchIds(carts.stream()
                        .map(OrdCart::getSkuId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(PrdSku::getId, Function.identity()));
        final Map<String, SalePublishSku> publishSkuMap = publishSkuMapper.selectList(
                        new LambdaQueryWrapper<SalePublishSku>()
                                .in(SalePublishSku::getPeriodId, carts.stream()
                                        .map(OrdCart::getPeriodId)
                                        .distinct()
                                        .toList())
                                .in(SalePublishSku::getSkuId, carts.stream()
                                        .map(OrdCart::getSkuId)
                                        .distinct()
                                        .toList()))
                .stream()
                .collect(Collectors.toMap(item -> item.getPeriodId() + ":" + item.getSkuId(), Function.identity(),
                        (left, right) -> left));
        return carts.stream()
                .map(cart -> enrichCart(cart, productMap.get(cart.getProductId()), skuMap.get(cart.getSkuId()),
                        publishSkuMap.get(cart.getPeriodId() + ":" + cart.getSkuId())))
                .toList();
    }

    private UserCartItemDTO enrichCart(
            final OrdCart cart,
            final PrdProduct product,
            final PrdSku sku,
            final SalePublishSku publishSku) {
        final UserCartItemDTO dto = userConvert.toCartItemDTO(cart);
        if (product != null) {
            dto.setProductName(product.getProductName());
            dto.setMainImageUrl(product.getMainImageUrl());
        }
        if (sku != null) {
            dto.setSkuName(sku.getSkuName());
        }
        if (publishSku != null) {
            dto.setSalePrice(publishSku.getSalePrice());
        }
        return dto;
    }

    private Long zeroIfNull(final Long value) {
        return value == null ? 0L : value;
    }
}
