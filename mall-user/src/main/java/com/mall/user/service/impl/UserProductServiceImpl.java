package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.api.user.dto.UserProductSkuDTO;
import com.mall.api.user.vo.UserProductQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.PrdCategory;
import com.mall.user.entity.PrdProduct;
import com.mall.user.entity.PrdSku;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.entity.SysFileAsset;
import com.mall.user.entity.UsrBrowseHistory;
import com.mall.user.entity.UsrComment;
import com.mall.user.entity.UsrUserFavorite;
import com.mall.user.mapper.PrdCategoryMapper;
import com.mall.user.mapper.PrdProductMapper;
import com.mall.user.mapper.PrdSkuMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
import com.mall.user.mapper.SysFileAssetMapper;
import com.mall.user.mapper.UsrBrowseHistoryMapper;
import com.mall.user.mapper.UsrCommentMapper;
import com.mall.user.mapper.UsrUserFavoriteMapper;
import com.mall.user.service.UserProductService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserProductServiceImpl extends UserReadSupport implements UserProductService {

    private static final Long CATEGORY_FRONT = 1L;
    private static final Long ENABLED = 1L;
    private static final Long PRODUCT_AUDIT_PASS = 20L;
    private static final Long PERIOD_ONLINE = 20L;
    private static final Long FAVORITE_ACTIVE = 1L;
    private static final Long COMMENT_SHOW = 1L;
    private static final String BIZ_CATEGORY_ICON = "USER_CATEGORY_ICON";
    private static final Set<String> ACTIVITY_CODES = Set.of(
            "HOME_MAIN",
            "ORCHARD_DIRECT",
            "TODAY_NEW",
            "BREAKFAST_BAKERY");

    private final PrdCategoryMapper categoryMapper;
    private final SysFileAssetMapper fileAssetMapper;
    private final PrdProductMapper productMapper;
    private final PrdSkuMapper skuMapper;
    private final SalePublishPeriodMapper periodMapper;
    private final SalePublishSkuMapper publishSkuMapper;
    private final UsrUserFavoriteMapper favoriteMapper;
    private final UsrBrowseHistoryMapper browseHistoryMapper;
    private final UsrCommentMapper commentMapper;
    private final UserConvert userConvert;

    public UserProductServiceImpl(
            final PrdCategoryMapper categoryMapper,
            final SysFileAssetMapper fileAssetMapper,
            final PrdProductMapper productMapper,
            final PrdSkuMapper skuMapper,
            final SalePublishPeriodMapper periodMapper,
            final SalePublishSkuMapper publishSkuMapper,
            final UsrUserFavoriteMapper favoriteMapper,
            final UsrBrowseHistoryMapper browseHistoryMapper,
            final UsrCommentMapper commentMapper,
            final UserConvert userConvert) {
        this.categoryMapper = categoryMapper;
        this.fileAssetMapper = fileAssetMapper;
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.periodMapper = periodMapper;
        this.publishSkuMapper = publishSkuMapper;
        this.favoriteMapper = favoriteMapper;
        this.browseHistoryMapper = browseHistoryMapper;
        this.commentMapper = commentMapper;
        this.userConvert = userConvert;
    }

    @Override
    public PageResult<CategoryDTO> pageCategories(final UserProductQueryVO query) {
        final UserProductQueryVO safeQuery = query == null ? new UserProductQueryVO() : query;
        final LambdaQueryWrapper<PrdCategory> wrapper = new LambdaQueryWrapper<PrdCategory>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(PrdCategory::getCategoryCode, safeQuery.getKeyword())
                        .or()
                        .like(PrdCategory::getCategoryName, safeQuery.getKeyword()))
                .eq(PrdCategory::getCategoryType, CATEGORY_FRONT)
                .eq(PrdCategory::getStatus, ENABLED)
                .orderByAsc(PrdCategory::getSortNo)
                .orderByDesc(PrdCategory::getId);
        final Map<String, String> iconMap = fileAssetMapper.selectList(new LambdaQueryWrapper<SysFileAsset>()
                        .eq(SysFileAsset::getBizType, BIZ_CATEGORY_ICON)
                .eq(SysFileAsset::getStatus, ENABLED)
                .orderByDesc(SysFileAsset::getId))
                .stream()
                .filter(item -> item.getBizNo() != null && item.getFileUrl() != null)
                .collect(Collectors.toMap(SysFileAsset::getBizNo, SysFileAsset::getFileUrl, (left, right) -> left));
        return toPage(safeQuery, categoryMapper, wrapper, item -> {
            final CategoryDTO dto = userConvert.toCategoryDTO(item);
            dto.setImageUrl(iconMap.get(item.getCategoryCode()));
            return dto;
        });
    }

    @Override
    public PageResult<UserProductCardDTO> pageProducts(final UserProductQueryVO query) {
        final UserProductQueryVO safeQuery = query == null ? new UserProductQueryVO() : query;
        final List<SalePublishPeriod> periods = listOnlinePeriods(safeQuery);
        if (periods.isEmpty()) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final Map<Long, SalePublishPeriod> periodMap = periods.stream()
                .collect(Collectors.toMap(SalePublishPeriod::getId, Function.identity(), (left, right) -> left));
        final Page<SalePublishSku> page = publishSkuMapper.selectPage(Page.of(safeQuery.getPageNum(),
                safeQuery.getPageSize()), buildPublishSkuWrapper(safeQuery, periodMap.keySet().stream().toList()));
        final List<SalePublishSku> records = page.getRecords();
        if (records.isEmpty()) {
            return PageResult.of(page.getTotal(), Collections.emptyList());
        }
        final Map<Long, PrdProduct> productMap = productMapper.selectBatchIds(records.stream()
                        .map(SalePublishSku::getProductId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(PrdProduct::getId, Function.identity()));
        final Map<Long, PrdSku> skuMap = skuMapper.selectBatchIds(records.stream()
                        .map(SalePublishSku::getSkuId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(PrdSku::getId, Function.identity()));
        final List<UserProductCardDTO> list = records.stream()
                .filter(item -> productMap.containsKey(item.getProductId()) && skuMap.containsKey(item.getSkuId()))
                .filter(item -> matchProduct(safeQuery, productMap.get(item.getProductId())))
                .map(item -> toProductCard(item, productMap.get(item.getProductId()), skuMap.get(item.getSkuId()),
                        periodMap.get(item.getPeriodId())))
                .toList();
        return PageResult.of(page.getTotal(), list);
    }

    @Override
    public PageResult<UserProductCardDTO> pageActivityProducts(final String activityCode, final UserProductQueryVO query) {
        if (!StringUtils.hasText(activityCode) || !ACTIVITY_CODES.contains(activityCode)) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final UserProductQueryVO safeQuery = copyQuery(query);
        applyActivityFilter(activityCode, safeQuery);
        return pageProducts(safeQuery);
    }

    @Override
    @Transactional
    public UserProductDetailDTO getProductDetail(final Long id, final UserProductQueryVO query) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        final PrdProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "商品不存在");
        }
        if (!PRODUCT_AUDIT_PASS.equals(product.getAuditStatus()) || !ENABLED.equals(product.getSaleStatus())) {
            throw new BusinessException(CommonErrorCode.CONFLICT, "商品未上架");
        }
        final UserProductQueryVO safeQuery = query == null ? new UserProductQueryVO() : query;
        safeQuery.setProductId(id);
        final UserProductDetailDTO detail = new UserProductDetailDTO();
        detail.setProduct(userConvert.toProductDTO(product));
        detail.setSkus(listProductSkus(safeQuery));
        detail.setFavoriteFlag(countFavorite(id, safeQuery.getUserId()) > 0L ? 1L : 0L);
        detail.setCommentCount(commentMapper.selectCount(new LambdaQueryWrapper<UsrComment>()
                .eq(UsrComment::getProductId, id)
                .eq(UsrComment::getStatus, COMMENT_SHOW)));
        appendBrowseHistory(id, safeQuery);
        return detail;
    }

    @Override
    public PageResult<UserCommentDTO> pageComments(final UserProductQueryVO query) {
        final UserProductQueryVO safeQuery = query == null ? new UserProductQueryVO() : query;
        final LambdaQueryWrapper<UsrComment> wrapper = new LambdaQueryWrapper<UsrComment>()
                .eq(safeQuery.getProductId() != null, UsrComment::getProductId, safeQuery.getProductId())
                .eq(safeQuery.getStationId() != null, UsrComment::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getCityId() != null, UsrComment::getCityId, safeQuery.getCityId())
                .eq(UsrComment::getStatus, COMMENT_SHOW)
                .orderByDesc(UsrComment::getId);
        return toPage(safeQuery, commentMapper, wrapper, userConvert::toCommentDTO);
    }

    private List<SalePublishPeriod> listOnlinePeriods(final UserProductQueryVO query) {
        final LocalDateTime now = LocalDateTime.now();
        return periodMapper.selectList(new LambdaQueryWrapper<SalePublishPeriod>()
                .eq(query.getPeriodId() != null, SalePublishPeriod::getId, query.getPeriodId())
                .eq(query.getCityId() != null, SalePublishPeriod::getCityId, query.getCityId())
                .eq(query.getDeliveryDate() != null, SalePublishPeriod::getDeliveryDate, query.getDeliveryDate())
                .eq(SalePublishPeriod::getStatus, PERIOD_ONLINE)
                .le(SalePublishPeriod::getSaleStartTime, now)
                .ge(SalePublishPeriod::getActualCutoffTime, now)
                .orderByDesc(SalePublishPeriod::getId));
    }

    private LambdaQueryWrapper<SalePublishSku> buildPublishSkuWrapper(
            final UserProductQueryVO query,
            final List<Long> periodIds) {
        return new LambdaQueryWrapper<SalePublishSku>()
                .in(SalePublishSku::getPeriodId, periodIds)
                .eq(query.getProductId() != null, SalePublishSku::getProductId, query.getProductId())
                .eq(SalePublishSku::getStatus, ENABLED)
                .orderByDesc(SalePublishSku::getId);
    }

    private Boolean matchProduct(final UserProductQueryVO query, final PrdProduct product) {
        if (product == null) {
            return false;
        }
        if (!PRODUCT_AUDIT_PASS.equals(product.getAuditStatus()) || !ENABLED.equals(product.getSaleStatus())) {
            return false;
        }
        if (query.getCategoryId() != null && !query.getCategoryId().equals(product.getFrontCategoryId())) {
            return false;
        }
        if (!StringUtils.hasText(query.getKeyword())) {
            return true;
        }
        return product.getProductName() != null && product.getProductName().contains(query.getKeyword());
    }

    private UserProductQueryVO copyQuery(final UserProductQueryVO query) {
        final UserProductQueryVO source = query == null ? new UserProductQueryVO() : query;
        final UserProductQueryVO target = new UserProductQueryVO();
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setUserId(source.getUserId());
        target.setCityId(source.getCityId());
        target.setStationId(source.getStationId());
        target.setCategoryId(source.getCategoryId());
        target.setProductId(source.getProductId());
        target.setPeriodId(source.getPeriodId());
        target.setKeyword(source.getKeyword());
        target.setDeliveryDate(source.getDeliveryDate());
        return target;
    }

    private void applyActivityFilter(final String activityCode, final UserProductQueryVO query) {
        if ("ORCHARD_DIRECT".equals(activityCode) || "HOME_MAIN".equals(activityCode)) {
            applyKeywordIfEmpty(query, "果");
            return;
        }
        if ("TODAY_NEW".equals(activityCode)) {
            return;
        }
        if ("BREAKFAST_BAKERY".equals(activityCode)) {
            applyKeywordIfEmpty(query, "早餐");
        }
    }

    private void applyKeywordIfEmpty(final UserProductQueryVO query, final String keyword) {
        if (!StringUtils.hasText(query.getKeyword())) {
            query.setKeyword(keyword);
        }
    }

    private UserProductCardDTO toProductCard(
            final SalePublishSku publishSku,
            final PrdProduct product,
            final PrdSku sku,
            final SalePublishPeriod period) {
        final UserProductCardDTO dto = new UserProductCardDTO();
        dto.setPublishSkuId(publishSku.getId());
        dto.setPeriodId(publishSku.getPeriodId());
        dto.setProductId(product.getId());
        dto.setSkuId(sku.getId());
        dto.setProductName(product.getProductName());
        dto.setSkuName(sku.getSkuName());
        dto.setMainImageUrl(product.getMainImageUrl());
        dto.setSaleSpecText(sku.getSaleSpecText());
        dto.setSalePrice(publishSku.getSalePrice());
        dto.setLimitQty(publishSku.getLimitQty());
        dto.setMinBuyQty(publishSku.getMinBuyQty());
        dto.setPlannedStockQty(zeroIfNull(publishSku.getPlannedStockQty()));
        dto.setSoldQty(zeroIfNull(publishSku.getSoldQty()));
        dto.setLockedQty(zeroIfNull(publishSku.getLockedQty()));
        dto.setAvailableQty(Math.max(0L, dto.getPlannedStockQty() - dto.getSoldQty() - dto.getLockedQty()));
        if (period != null) {
            dto.setDeliveryDate(period.getDeliveryDate());
            dto.setSaleEndTime(period.getSaleEndTime());
        }
        return dto;
    }

    private List<UserProductSkuDTO> listProductSkus(final UserProductQueryVO query) {
        final List<SalePublishPeriod> periods = listOnlinePeriods(query);
        if (periods.isEmpty()) {
            return Collections.emptyList();
        }
        final Map<Long, SalePublishPeriod> periodMap = periods.stream()
                .collect(Collectors.toMap(SalePublishPeriod::getId, Function.identity(), (left, right) -> left));
        return publishSkuMapper.selectList(buildPublishSkuWrapper(query, periodMap.keySet().stream().toList()))
                .stream()
                .map(item -> toProductSku(item, skuMapper.selectById(item.getSkuId()),
                        periodMap.get(item.getPeriodId())))
                .filter(item -> item != null)
                .toList();
    }

    private UserProductSkuDTO toProductSku(
            final SalePublishSku publishSku,
            final PrdSku sku,
            final SalePublishPeriod period) {
        if (sku == null || !ENABLED.equals(sku.getStatus())) {
            return null;
        }
        final UserProductSkuDTO dto = new UserProductSkuDTO();
        dto.setPublishSkuId(publishSku.getId());
        dto.setPeriodId(publishSku.getPeriodId());
        dto.setSkuId(sku.getId());
        dto.setSkuName(sku.getSkuName());
        dto.setSaleSpecText(sku.getSaleSpecText());
        dto.setBaseUnit(sku.getBaseUnit());
        dto.setSalePrice(publishSku.getSalePrice());
        dto.setLimitQty(publishSku.getLimitQty());
        dto.setMinBuyQty(publishSku.getMinBuyQty());
        dto.setAvailableQty(Math.max(0L, zeroIfNull(publishSku.getPlannedStockQty())
                - zeroIfNull(publishSku.getSoldQty()) - zeroIfNull(publishSku.getLockedQty())));
        if (period != null) {
            dto.setDeliveryDate(period.getDeliveryDate());
        }
        return dto;
    }

    private Long countFavorite(final Long productId, final Long userId) {
        if (userId == null) {
            return 0L;
        }
        return favoriteMapper.selectCount(new LambdaQueryWrapper<UsrUserFavorite>()
                .eq(UsrUserFavorite::getUserId, userId)
                .eq(UsrUserFavorite::getProductId, productId)
                .eq(UsrUserFavorite::getStatus, FAVORITE_ACTIVE));
    }

    private void appendBrowseHistory(final Long productId, final UserProductQueryVO query) {
        if (query.getUserId() == null) {
            return;
        }
        final LocalDateTime now = LocalDateTime.now();
        final UsrBrowseHistory history = new UsrBrowseHistory();
        prepareCreate(history, com.baomidou.mybatisplus.core.toolkit.IdWorker.getId(), now);
        history.setUserId(query.getUserId());
        history.setProductId(productId);
        history.setSkuId(null);
        history.setStationId(query.getStationId());
        history.setBrowseTime(now);
        browseHistoryMapper.insert(history);
    }

    private Long zeroIfNull(final Long value) {
        return value == null ? 0L : value;
    }
}
