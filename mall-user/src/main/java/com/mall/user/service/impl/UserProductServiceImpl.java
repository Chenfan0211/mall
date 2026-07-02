package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.api.user.dto.UserProductPurchaseRecordDTO;
import com.mall.api.user.dto.UserProductReviewStatsDTO;
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
import com.mall.user.mapper.OrdOrderItemMapper;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    private final OrdOrderItemMapper orderItemMapper;
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
            final OrdOrderItemMapper orderItemMapper,
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
        this.orderItemMapper = orderItemMapper;
        this.userConvert = userConvert;
    }

    @Override
    public PageResult<CategoryDTO> pageCategories(final UserProductQueryVO query) {
        final UserProductQueryVO safeQuery = safeQuery(query);
        final Set<Long> onlineCategoryIds = listOnlineCategoryIds(safeQuery);
        if (onlineCategoryIds.isEmpty()) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final List<PrdCategory> categories = categoryMapper.selectList(new LambdaQueryWrapper<PrdCategory>()
                .eq(PrdCategory::getCategoryType, CATEGORY_FRONT)
                .eq(PrdCategory::getStatus, ENABLED)
                .orderByAsc(PrdCategory::getSortNo)
                .orderByAsc(PrdCategory::getId));
        final Map<Long, PrdCategory> categoryMap = categories.stream()
                .collect(Collectors.toMap(PrdCategory::getId, Function.identity(), (left, right) -> left));
        final Set<Long> onlineRootIds = onlineCategoryIds.stream()
                .map(categoryId -> rootCategoryId(categoryMap, categoryId))
                .filter(id -> id != null && categoryMap.containsKey(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (onlineRootIds.isEmpty()) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final Map<String, String> iconMap = fileAssetMapper.selectList(new LambdaQueryWrapper<SysFileAsset>()
                        .eq(SysFileAsset::getBizType, BIZ_CATEGORY_ICON)
                        .eq(SysFileAsset::getStatus, ENABLED)
                        .orderByDesc(SysFileAsset::getId))
                .stream()
                .filter(item -> item.getBizNo() != null && item.getFileUrl() != null)
                .collect(Collectors.toMap(SysFileAsset::getBizNo, SysFileAsset::getFileUrl, (left, right) -> left));
        final List<PrdCategory> matchedRoots = categories.stream()
                .filter(item -> onlineRootIds.contains(item.getId()))
                .filter(item -> categoryMatchesKeyword(item, safeQuery.getKeyword()))
                .toList();
        final long pageNum = safeQuery.getPageNum() == null ? 1L : safeQuery.getPageNum();
        final long pageSize = safeQuery.getPageSize() == null ? 20L : safeQuery.getPageSize();
        final int fromIndex = (int) Math.min(matchedRoots.size(), Math.max(0L, (pageNum - 1L) * pageSize));
        final int toIndex = (int) Math.min(matchedRoots.size(), fromIndex + pageSize);
        final List<CategoryDTO> pageList = matchedRoots.subList(fromIndex, toIndex).stream()
                .map(item -> toCategoryDTO(item, iconMap, buildAvailableChildren(categories, onlineCategoryIds, item.getId(), iconMap)))
                .toList();
        return PageResult.of((long) matchedRoots.size(), pageList);
    }

    @Override
    public PageResult<UserProductCardDTO> pageProducts(final UserProductQueryVO query) {
        final UserProductQueryVO safeQuery = safeQuery(query);
        final List<SalePublishPeriod> periods = listOnlinePeriods(safeQuery);
        if (periods.isEmpty()) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final Set<Long> productIds = listMatchedProductIds(safeQuery);
        if (productIds.isEmpty()) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final Map<Long, SalePublishPeriod> periodMap = periods.stream()
                .collect(Collectors.toMap(SalePublishPeriod::getId, Function.identity(), (left, right) -> left));
        if (Boolean.TRUE.equals(safeQuery.getMergeSku())) {
            return pageMergedSkuProducts(safeQuery, periodMap, productIds);
        }
        final Page<SalePublishSku> page = publishSkuMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()),
                buildPublishSkuWrapper(safeQuery, periodMap.keySet().stream().toList(), productIds));
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
                .map(item -> toProductCard(
                        item,
                        productMap.get(item.getProductId()),
                        skuMap.get(item.getSkuId()),
                        periodMap.get(item.getPeriodId())))
                .toList();
        return PageResult.of(page.getTotal(), list);
    }

    private PageResult<UserProductCardDTO> pageMergedSkuProducts(
            final UserProductQueryVO query,
            final Map<Long, SalePublishPeriod> periodMap,
            final Set<Long> productIds) {
        final List<SalePublishSku> publishSkus = publishSkuMapper.selectList(new LambdaQueryWrapper<SalePublishSku>()
                .in(SalePublishSku::getPeriodId, periodMap.keySet().stream().toList())
                .in(productIds != null && !productIds.isEmpty(), SalePublishSku::getProductId, productIds)
                .eq(query.getProductId() != null, SalePublishSku::getProductId, query.getProductId())
                .eq(SalePublishSku::getStatus, ENABLED));
        if (publishSkus.isEmpty()) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final Map<Long, PrdProduct> productMap = productMapper.selectBatchIds(publishSkus.stream()
                        .map(SalePublishSku::getProductId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(PrdProduct::getId, Function.identity()));
        final Map<Long, PrdSku> skuMap = skuMapper.selectBatchIds(publishSkus.stream()
                        .map(SalePublishSku::getSkuId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(PrdSku::getId, Function.identity()));
        final List<UserProductCardDTO> mergedList = publishSkus.stream()
                .filter(item -> productMap.containsKey(item.getProductId()) && skuMap.containsKey(item.getSkuId()))
                .collect(Collectors.groupingBy(SalePublishSku::getProductId, LinkedHashMap::new, Collectors.toList()))
                .values()
                .stream()
                .map(items -> toMergedProductCard(items, productMap, skuMap, periodMap))
                .filter(item -> item != null)
                .sorted(mergedProductComparator(query))
                .toList();
        final long pageNum = query.getPageNum() == null ? 1L : query.getPageNum();
        final long pageSize = query.getPageSize() == null ? 20L : query.getPageSize();
        final int fromIndex = (int) Math.min(mergedList.size(), Math.max(0L, (pageNum - 1L) * pageSize));
        final int toIndex = (int) Math.min(mergedList.size(), fromIndex + pageSize);
        return PageResult.of((long) mergedList.size(), mergedList.subList(fromIndex, toIndex));
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
        final UserProductQueryVO safeQuery = safeQuery(query);
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
    public UserProductReviewStatsDTO getReviewStats(final Long id, final UserProductQueryVO query) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        final UserProductQueryVO safeQuery = safeQuery(query);
        final LocalDateTime since = LocalDateTime.now().minusDays(30L);
        final List<UsrComment> comments = commentMapper.selectList(new LambdaQueryWrapper<UsrComment>()
                .eq(UsrComment::getProductId, id)
                .eq(safeQuery.getCityId() != null, UsrComment::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStationId() != null, UsrComment::getStationId, safeQuery.getStationId())
                .eq(UsrComment::getStatus, COMMENT_SHOW));
        final long commentCount = comments.size();
        final long goodCount = comments.stream()
                .filter(item -> zeroIfNull(item.getScore()) >= 4L)
                .count();
        final long imageReviewCount = comments.stream()
                .filter(item -> hasReviewImage(item.getImageJson()))
                .count();
        final UserProductReviewStatsDTO result = new UserProductReviewStatsDTO();
        result.setCommentCount(commentCount);
        result.setGoodRatePercent(commentCount == 0L
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(goodCount * 100L).divide(BigDecimal.valueOf(commentCount), 1, RoundingMode.HALF_UP));
        result.setImageReviewCount(imageReviewCount);
        result.setRecentSoldQty(zeroIfNull(orderItemMapper.sumRecentSoldQty(
                id, safeQuery.getCityId(), safeQuery.getStationId(), since)));
        result.setRecentRepurchaseUserCount(zeroIfNull(orderItemMapper.countRecentRepurchaseUsers(
                id, safeQuery.getCityId(), safeQuery.getStationId(), since)));
        result.setTags(List.of(
                new UserProductReviewStatsDTO.TagCountDTO("新鲜/色泽好", countTag(comments, Set.of("新鲜", "鲜", "色泽", "水灵"))),
                new UserProductReviewStatsDTO.TagCountDTO("包装完整", countTag(comments, Set.of("包装", "完整", "完好"))),
                new UserProductReviewStatsDTO.TagCountDTO("提货方便", countTag(comments, Set.of("提货", "方便", "自提"))),
                new UserProductReviewStatsDTO.TagCountDTO("分量足", countTag(comments, Set.of("分量", "足", "量大", "够吃"))))
                .stream()
                .filter(item -> zeroIfNull(item.getCount()) > 0L)
                .toList());
        return result;
    }

    @Override
    public PageResult<UserCommentDTO> pageComments(final UserProductQueryVO query) {
        final UserProductQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<UsrComment> wrapper = new LambdaQueryWrapper<UsrComment>()
                .eq(safeQuery.getProductId() != null, UsrComment::getProductId, safeQuery.getProductId())
                .eq(safeQuery.getStationId() != null, UsrComment::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getCityId() != null, UsrComment::getCityId, safeQuery.getCityId())
                .eq(UsrComment::getStatus, COMMENT_SHOW)
                .isNotNull(Boolean.TRUE.equals(safeQuery.getHasImage()), UsrComment::getImageJson)
                .ne(Boolean.TRUE.equals(safeQuery.getHasImage()), UsrComment::getImageJson, "")
                .ne(Boolean.TRUE.equals(safeQuery.getHasImage()), UsrComment::getImageJson, "[]");
        if ("latest".equalsIgnoreCase(safeQuery.getSortField())) {
            wrapper.orderBy(true, !"asc".equalsIgnoreCase(safeQuery.getSortOrder()), UsrComment::getCreateTime);
        }
        wrapper.orderByDesc(UsrComment::getId);
        return toPage(safeQuery, commentMapper, wrapper, userConvert::toCommentDTO);
    }

    @Override
    public PageResult<UserProductPurchaseRecordDTO> pagePurchaseRecords(final Long id, final UserProductQueryVO query) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        final UserProductQueryVO safeQuery = safeQuery(query);
        final long pageNum = safeQuery.getPageNum() == null ? 1L : safeQuery.getPageNum();
        final long pageSize = safeQuery.getPageSize() == null ? 20L : safeQuery.getPageSize();
        final long offset = Math.max(0L, pageNum - 1L) * pageSize;
        final Long total = orderItemMapper.countPurchaseRecords(id, safeQuery.getCityId(), safeQuery.getStationId());
        if (zeroIfNull(total) == 0L) {
            return PageResult.of(0L, Collections.emptyList());
        }
        final List<UserProductPurchaseRecordDTO> list = orderItemMapper
                .listPurchaseRecords(id, safeQuery.getCityId(), safeQuery.getStationId(), offset, pageSize)
                .stream()
                .peek(item -> item.setUserName(maskUserName(item.getUserName())))
                .toList();
        return PageResult.of(total, list);
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
            final List<Long> periodIds,
            final Set<Long> productIds) {
        final LambdaQueryWrapper<SalePublishSku> wrapper = new LambdaQueryWrapper<SalePublishSku>()
                .in(SalePublishSku::getPeriodId, periodIds)
                .in(productIds != null && !productIds.isEmpty(), SalePublishSku::getProductId, productIds)
                .eq(query.getProductId() != null, SalePublishSku::getProductId, query.getProductId())
                .eq(SalePublishSku::getStatus, ENABLED);
        applyProductSort(wrapper, query);
        return wrapper;
    }

    private UserProductQueryVO safeQuery(final UserProductQueryVO query) {
        return query == null ? new UserProductQueryVO() : query;
    }

    private UserProductQueryVO copyQuery(final UserProductQueryVO query) {
        final UserProductQueryVO source = safeQuery(query);
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
        target.setSortField(source.getSortField());
        target.setSortOrder(source.getSortOrder());
        return target;
    }

    private void applyActivityFilter(final String activityCode, final UserProductQueryVO query) {
        if ("ORCHARD_DIRECT".equals(activityCode) || "HOME_MAIN".equals(activityCode)) {
            applyKeywordIfEmpty(query, "果");
            return;
        }
        if ("TODAY_NEW".equals(activityCode)) {
            query.setSortField("new");
            query.setSortOrder("desc");
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

    private void applyProductSort(final LambdaQueryWrapper<SalePublishSku> wrapper, final UserProductQueryVO query) {
        final String sortField = query.getSortField();
        final boolean asc = "asc".equalsIgnoreCase(query.getSortOrder());
        if ("sales".equalsIgnoreCase(sortField)) {
            wrapper.orderBy(true, asc, SalePublishSku::getSoldQty);
        } else if ("price".equalsIgnoreCase(sortField)) {
            wrapper.orderBy(true, asc, SalePublishSku::getSalePrice);
        }
        wrapper.orderByDesc(SalePublishSku::getId);
    }

    private Set<Long> listMatchedProductIds(final UserProductQueryVO query) {
        final Set<Long> categoryIds = resolveCategoryScope(query.getCategoryId());
        final List<PrdProduct> products = productMapper.selectList(new LambdaQueryWrapper<PrdProduct>()
                .eq(query.getProductId() != null, PrdProduct::getId, query.getProductId())
                .in(!categoryIds.isEmpty(), PrdProduct::getFrontCategoryId, categoryIds)
                .and(StringUtils.hasText(query.getKeyword()), nested -> nested
                        .like(PrdProduct::getProductName, query.getKeyword())
                        .or()
                        .like(PrdProduct::getProductNo, query.getKeyword()))
                .eq(PrdProduct::getAuditStatus, PRODUCT_AUDIT_PASS)
                .eq(PrdProduct::getSaleStatus, ENABLED));
        return products.stream().map(PrdProduct::getId).collect(Collectors.toSet());
    }

    private Set<Long> listOnlineCategoryIds(final UserProductQueryVO query) {
        final List<SalePublishPeriod> periods = listOnlinePeriods(query);
        if (periods.isEmpty()) {
            return Collections.emptySet();
        }
        final List<SalePublishSku> publishSkus = publishSkuMapper.selectList(new LambdaQueryWrapper<SalePublishSku>()
                .in(SalePublishSku::getPeriodId, periods.stream().map(SalePublishPeriod::getId).toList())
                .eq(SalePublishSku::getStatus, ENABLED));
        final Set<Long> productIds = publishSkus.stream()
                .filter(this::hasAvailableStock)
                .map(SalePublishSku::getProductId)
                .collect(Collectors.toSet());
        if (productIds.isEmpty()) {
            return Collections.emptySet();
        }
        return productMapper.selectBatchIds(productIds).stream()
                .filter(product -> PRODUCT_AUDIT_PASS.equals(product.getAuditStatus()))
                .filter(product -> ENABLED.equals(product.getSaleStatus()))
                .filter(product -> product.getFrontCategoryId() != null)
                .map(PrdProduct::getFrontCategoryId)
                .collect(Collectors.toSet());
    }

    private Set<Long> resolveCategoryScope(final Long categoryId) {
        if (categoryId == null) {
            return Collections.emptySet();
        }
        final PrdCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return Set.of(categoryId);
        }
        if (zeroIfNull(category.getParentId()) > 0L) {
            return Set.of(categoryId);
        }
        final Set<Long> ids = new HashSet<>();
        ids.add(categoryId);
        ids.addAll(categoryMapper.selectList(new LambdaQueryWrapper<PrdCategory>()
                        .eq(PrdCategory::getParentId, categoryId)
                        .eq(PrdCategory::getCategoryType, CATEGORY_FRONT)
                        .eq(PrdCategory::getStatus, ENABLED))
                .stream()
                .map(PrdCategory::getId)
                .collect(Collectors.toSet()));
        return ids;
    }

    private boolean categoryMatchesKeyword(final PrdCategory category, final String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        return containsText(category.getCategoryCode(), keyword) || containsText(category.getCategoryName(), keyword);
    }

    private boolean containsText(final String source, final String keyword) {
        return source != null && source.contains(keyword);
    }

    private Long rootCategoryId(final Map<Long, PrdCategory> categoryMap, final Long categoryId) {
        final PrdCategory category = categoryMap.get(categoryId);
        if (category == null) {
            return categoryId;
        }
        final Long parentId = category.getParentId();
        if (parentId == null || parentId == 0L) {
            return category.getId();
        }
        return categoryMap.containsKey(parentId) ? parentId : category.getId();
    }

    private List<CategoryDTO> buildAvailableChildren(
            final List<PrdCategory> categories,
            final Set<Long> onlineCategoryIds,
            final Long parentId,
            final Map<String, String> iconMap) {
        return categories.stream()
                .filter(item -> parentId.equals(item.getParentId()))
                .filter(item -> onlineCategoryIds.contains(item.getId()))
                .sorted(Comparator.comparing(PrdCategory::getSortNo).thenComparing(PrdCategory::getId))
                .map(item -> toCategoryDTO(item, iconMap, Collections.emptyList()))
                .toList();
    }

    private CategoryDTO toCategoryDTO(
            final PrdCategory item,
            final Map<String, String> iconMap,
            final List<CategoryDTO> children) {
        final CategoryDTO dto = userConvert.toCategoryDTO(item);
        dto.setImageUrl(iconMap.get(item.getCategoryCode()));
        dto.setChildren(new ArrayList<>(children));
        return dto;
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
        dto.setSkuCount(1L);
        dto.setAvailableSkuCount(dto.getAvailableQty() > 0L ? 1L : 0L);
        dto.setMinSalePrice(publishSku.getSalePrice());
        dto.setMaxSalePrice(publishSku.getSalePrice());
        if (period != null) {
            dto.setDeliveryDate(period.getDeliveryDate());
            dto.setSaleEndTime(period.getSaleEndTime());
        }
        return dto;
    }

    private UserProductCardDTO toMergedProductCard(
            final List<SalePublishSku> publishSkus,
            final Map<Long, PrdProduct> productMap,
            final Map<Long, PrdSku> skuMap,
            final Map<Long, SalePublishPeriod> periodMap) {
        if (publishSkus.isEmpty()) {
            return null;
        }
        final SalePublishSku representative = publishSkus.stream()
                .filter(this::hasAvailableStock)
                .min(Comparator.comparing(SalePublishSku::getSalePrice, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(SalePublishSku::getId, Comparator.nullsLast(Long::compareTo)))
                .orElseGet(() -> publishSkus.stream()
                        .min(Comparator.comparing(SalePublishSku::getSalePrice, Comparator.nullsLast(BigDecimal::compareTo))
                                .thenComparing(SalePublishSku::getId, Comparator.nullsLast(Long::compareTo)))
                        .orElse(publishSkus.get(0)));
        final PrdProduct product = productMap.get(representative.getProductId());
        final PrdSku sku = skuMap.get(representative.getSkuId());
        if (product == null || sku == null) {
            return null;
        }
        final UserProductCardDTO dto = toProductCard(representative, product, sku, periodMap.get(representative.getPeriodId()));
        final long soldQty = publishSkus.stream().mapToLong(item -> zeroIfNull(item.getSoldQty())).sum();
        final long lockedQty = publishSkus.stream().mapToLong(item -> zeroIfNull(item.getLockedQty())).sum();
        final long plannedQty = publishSkus.stream().mapToLong(item -> zeroIfNull(item.getPlannedStockQty())).sum();
        final List<SalePublishSku> priceSkus = publishSkus.stream()
                .filter(this::hasAvailableStock)
                .toList();
        final List<SalePublishSku> effectivePriceSkus = priceSkus.isEmpty() ? publishSkus : priceSkus;
        final BigDecimal minPrice = effectivePriceSkus.stream()
                .map(SalePublishSku::getSalePrice)
                .filter(price -> price != null)
                .min(BigDecimal::compareTo)
                .orElse(dto.getSalePrice());
        final BigDecimal maxPrice = effectivePriceSkus.stream()
                .map(SalePublishSku::getSalePrice)
                .filter(price -> price != null)
                .max(BigDecimal::compareTo)
                .orElse(dto.getSalePrice());
        dto.setSalePrice(minPrice);
        dto.setMinSalePrice(minPrice);
        dto.setMaxSalePrice(maxPrice);
        dto.setPlannedStockQty(plannedQty);
        dto.setSoldQty(soldQty);
        dto.setLockedQty(lockedQty);
        dto.setAvailableQty(Math.max(0L, plannedQty - soldQty - lockedQty));
        dto.setSkuCount((long) publishSkus.size());
        dto.setAvailableSkuCount(publishSkus.stream().filter(this::hasAvailableStock).count());
        return dto;
    }

    private Comparator<UserProductCardDTO> mergedProductComparator(final UserProductQueryVO query) {
        final boolean asc = "asc".equalsIgnoreCase(query.getSortOrder());
        Comparator<UserProductCardDTO> comparator;
        if ("sales".equalsIgnoreCase(query.getSortField())) {
            comparator = Comparator.comparing(UserProductCardDTO::getSoldQty, Comparator.nullsLast(Long::compareTo));
        } else if ("price".equalsIgnoreCase(query.getSortField())) {
            comparator = Comparator.comparing(UserProductCardDTO::getSalePrice, Comparator.nullsLast(BigDecimal::compareTo));
        } else {
            comparator = Comparator.comparing(UserProductCardDTO::getPublishSkuId, Comparator.nullsLast(Long::compareTo));
        }
        if (!asc) {
            comparator = comparator.reversed();
        }
        return Comparator.comparing((UserProductCardDTO item) -> zeroIfNull(item.getAvailableQty()) > 0L ? 0 : 1)
                .thenComparing(comparator)
                .thenComparing(UserProductCardDTO::getProductId, Comparator.nullsLast(Long::compareTo));
    }

    private List<UserProductSkuDTO> listProductSkus(final UserProductQueryVO query) {
        final List<SalePublishPeriod> periods = listOnlinePeriods(query);
        if (periods.isEmpty()) {
            return Collections.emptyList();
        }
        final Set<Long> productIds = listMatchedProductIds(query);
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        final Map<Long, SalePublishPeriod> periodMap = periods.stream()
                .collect(Collectors.toMap(SalePublishPeriod::getId, Function.identity(), (left, right) -> left));
        return publishSkuMapper.selectList(buildPublishSkuWrapper(query, periodMap.keySet().stream().toList(), productIds))
                .stream()
                .map(item -> toProductSku(item, skuMapper.selectById(item.getSkuId()), periodMap.get(item.getPeriodId())))
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

    private boolean hasAvailableStock(final SalePublishSku sku) {
        return zeroIfNull(sku.getPlannedStockQty()) > zeroIfNull(sku.getSoldQty()) + zeroIfNull(sku.getLockedQty());
    }

    private Long zeroIfNull(final Long value) {
        return value == null ? 0L : value;
    }

    private Long countTag(final List<UsrComment> comments, final Set<String> keywords) {
        return comments.stream()
                .map(UsrComment::getContent)
                .filter(StringUtils::hasText)
                .filter(content -> keywords.stream().anyMatch(content::contains))
                .count();
    }

    private boolean hasReviewImage(final String imageJson) {
        return StringUtils.hasText(imageJson) && !"[]".equals(imageJson.trim());
    }

    private String maskUserName(final String source) {
        if (!StringUtils.hasText(source)) {
            return "用户*";
        }
        final String value = source.trim();
        if (value.matches("^1\\d{10}$")) {
            return value.substring(0, 3) + "****" + value.substring(7);
        }
        if (value.length() <= 1) {
            return value + "*";
        }
        if (value.length() == 2) {
            return value.charAt(0) + "*";
        }
        return value.charAt(0) + "*" + value.charAt(value.length() - 1);
    }
}
