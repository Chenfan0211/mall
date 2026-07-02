package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.api.user.dto.UserProductPurchaseRecordDTO;
import com.mall.api.user.dto.UserProductReviewStatsDTO;
import com.mall.api.user.vo.UserProductQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.PrdCategory;
import com.mall.user.entity.PrdProduct;
import com.mall.user.entity.PrdSku;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.entity.SysFileAsset;
import com.mall.user.entity.UsrComment;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProductServiceImplTest {

    @Mock
    private PrdCategoryMapper categoryMapper;

    @Mock
    private SysFileAssetMapper fileAssetMapper;

    @Mock
    private PrdProductMapper productMapper;

    @Mock
    private PrdSkuMapper skuMapper;

    @Mock
    private SalePublishPeriodMapper periodMapper;

    @Mock
    private SalePublishSkuMapper publishSkuMapper;

    @Mock
    private UsrUserFavoriteMapper favoriteMapper;

    @Mock
    private UsrBrowseHistoryMapper browseHistoryMapper;

    @Mock
    private UsrCommentMapper commentMapper;

    @Mock
    private OrdOrderItemMapper orderItemMapper;

    @Mock
    private UserConvert userConvert;

    @InjectMocks
    private UserProductServiceImpl userProductService;

    @Test
    void pageCategories_onlyReturnCategoriesWithOnlineProducts() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setCityId(440100L);
        final SalePublishPeriod period = onlinePeriod();
        final SalePublishSku publishSku = publishSku();
        final PrdProduct product = product();
        final PrdCategory category = category();
        final CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(750001L);
        categoryDTO.setCategoryName("水果鲜食");
        final SysFileAsset icon = new SysFileAsset();
        icon.setBizNo("FRONT_FRUIT");
        icon.setFileUrl("/static/user-home/shop-home.jpg");

        when(periodMapper.selectList(any())).thenReturn(List.of(period));
        when(publishSkuMapper.selectList(any())).thenReturn(List.of(publishSku));
        when(productMapper.selectBatchIds(any())).thenReturn(List.of(product));
        when(categoryMapper.selectList(any())).thenReturn(List.of(category));
        when(fileAssetMapper.selectList(any())).thenReturn(List.of(icon));
        when(userConvert.toCategoryDTO(category)).thenReturn(categoryDTO);

        final PageResult<CategoryDTO> result = userProductService.pageCategories(query);

        assertEquals(1L, result.getTotal());
        assertEquals("/static/user-home/shop-home.jpg", result.getList().get(0).getImageUrl());
    }

    @Test
    void pageProducts_filtersBeforePagingAndReturnsProducts() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setCityId(440100L);
        query.setCategoryId(750001L);
        query.setSortField("price");
        query.setSortOrder("asc");
        final SalePublishPeriod period = onlinePeriod();
        final SalePublishSku publishSku = publishSku();
        final PrdProduct product = product();
        final PrdSku sku = sku();
        final Page<SalePublishSku> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(publishSku));

        when(periodMapper.selectList(any())).thenReturn(List.of(period));
        when(productMapper.selectList(any())).thenReturn(List.of(product));
        when(publishSkuMapper.selectPage(any(), any())).thenReturn(page);
        when(productMapper.selectBatchIds(any())).thenReturn(List.of(product));
        when(skuMapper.selectBatchIds(any())).thenReturn(List.of(sku));

        final PageResult<UserProductCardDTO> result = userProductService.pageProducts(query);

        assertEquals(1L, result.getTotal());
        assertEquals("赣南脐橙", result.getList().get(0).getProductName());
        assertEquals(8L, result.getList().get(0).getAvailableQty());
    }

    @Test
    void pageProducts_mergeSkuReturnsOneProductWithAggregatedStock() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setCityId(440100L);
        query.setMergeSku(true);
        query.setSortField("price");
        query.setSortOrder("asc");
        final SalePublishSku first = publishSku();
        final SalePublishSku second = publishSku();
        second.setId(815002L);
        second.setSkuId(814002L);
        second.setSalePrice(new BigDecimal("29.90"));
        second.setPlannedStockQty(20L);
        second.setSoldQty(2L);
        second.setLockedQty(3L);
        final PrdProduct product = product();
        final PrdSku firstSku = sku();
        final PrdSku secondSku = sku();
        secondSku.setId(814002L);
        secondSku.setSkuName("5斤装");

        when(periodMapper.selectList(any())).thenReturn(List.of(onlinePeriod()));
        when(productMapper.selectList(any())).thenReturn(List.of(product));
        when(publishSkuMapper.selectList(any())).thenReturn(List.of(first, second));
        when(productMapper.selectBatchIds(any())).thenReturn(List.of(product));
        when(skuMapper.selectBatchIds(any())).thenReturn(List.of(firstSku, secondSku));

        final PageResult<UserProductCardDTO> result = userProductService.pageProducts(query);

        assertEquals(1L, result.getTotal());
        assertEquals(2L, result.getList().get(0).getSkuCount());
        assertEquals(2L, result.getList().get(0).getAvailableSkuCount());
        assertEquals(23L, result.getList().get(0).getAvailableQty());
        assertEquals(new BigDecimal("19.90"), result.getList().get(0).getMinSalePrice());
        assertEquals(new BigDecimal("29.90"), result.getList().get(0).getMaxSalePrice());
    }

    @Test
    void getProductDetail_returnsMultipleSkus() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setUserId(740001L);
        query.setCityId(440100L);
        final PrdProduct product = product();
        final ProductDTO productDTO = new ProductDTO();
        productDTO.setId(813001L);
        productDTO.setProductName("赣南脐橙");
        when(productMapper.selectById(813001L)).thenReturn(product);
        when(periodMapper.selectList(any())).thenReturn(List.of(onlinePeriod()));
        when(productMapper.selectList(any())).thenReturn(List.of(product));
        when(publishSkuMapper.selectList(any())).thenReturn(List.of(publishSku()));
        when(skuMapper.selectById(814001L)).thenReturn(sku());
        when(favoriteMapper.selectCount(any())).thenReturn(1L);
        when(commentMapper.selectCount(any())).thenReturn(2L);
        when(userConvert.toProductDTO(product)).thenReturn(productDTO);

        final UserProductDetailDTO result = userProductService.getProductDetail(813001L, query);

        assertEquals(1L, result.getFavoriteFlag());
        assertEquals(1, result.getSkus().size());
    }

    @Test
    void getReviewStats_calculatesRecentStats() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setCityId(440100L);
        query.setStationId(720001L);
        final UsrComment good = comment(5L, "很新鲜，提货方便");
        good.setImageJson("[\"/static/user-home/shop-detail.jpg\"]");
        final UsrComment normal = comment(3L, "价格划算");
        final UsrComment packaging = comment(5L, "包装完整，分量足");
        when(commentMapper.selectList(any())).thenReturn(List.of(good, normal, packaging));
        when(orderItemMapper.sumRecentSoldQty(any(), any(), any(), any())).thenReturn(12L);
        when(orderItemMapper.countRecentRepurchaseUsers(any(), any(), any(), any())).thenReturn(3L);

        final UserProductReviewStatsDTO result = userProductService.getReviewStats(813001L, query);

        assertEquals(3L, result.getCommentCount());
        assertEquals(new BigDecimal("66.7"), result.getGoodRatePercent());
        assertEquals(1L, result.getImageReviewCount());
        assertEquals(12L, result.getRecentSoldQty());
        assertEquals(3L, result.getRecentRepurchaseUserCount());
        assertEquals(4, result.getTags().size());
    }

    @Test
    void pagePurchaseRecords_masksUserNameAndReturnsRecords() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setCityId(440100L);
        query.setStationId(720001L);
        query.setPageNum(1L);
        query.setPageSize(3L);
        final UserProductPurchaseRecordDTO record = new UserProductPurchaseRecordDTO();
        record.setUserName("张三丰");
        record.setProductName("赣南脐橙");
        record.setSkuName("3斤装");
        record.setQty(2L);
        when(orderItemMapper.countPurchaseRecords(813001L, 440100L, 720001L)).thenReturn(1L);
        when(orderItemMapper.listPurchaseRecords(813001L, 440100L, 720001L, 0L, 3L)).thenReturn(List.of(record));

        final PageResult<UserProductPurchaseRecordDTO> result = userProductService.pagePurchaseRecords(813001L, query);

        assertEquals(1L, result.getTotal());
        assertEquals("张*丰", result.getList().get(0).getUserName());
        assertEquals("赣南脐橙", result.getList().get(0).getProductName());
        assertEquals("3斤装", result.getList().get(0).getSkuName());
        assertEquals(2L, result.getList().get(0).getQty());
    }

    @Test
    void getProductDetail_notFound_throwNotFound() {
        when(productMapper.selectById(999999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userProductService.getProductDetail(999999L, new UserProductQueryVO()));
    }

    private SalePublishPeriod onlinePeriod() {
        final SalePublishPeriod period = new SalePublishPeriod();
        period.setId(810900L);
        period.setCityId(440100L);
        period.setDeliveryDate(LocalDate.now().plusDays(1L));
        period.setSaleStartTime(LocalDateTime.now().minusHours(1L));
        period.setActualCutoffTime(LocalDateTime.now().plusHours(1L));
        period.setStatus(20L);
        return period;
    }

    private SalePublishSku publishSku() {
        final SalePublishSku publishSku = new SalePublishSku();
        publishSku.setId(815001L);
        publishSku.setPeriodId(810900L);
        publishSku.setProductId(813001L);
        publishSku.setSkuId(814001L);
        publishSku.setSalePrice(new BigDecimal("19.90"));
        publishSku.setPlannedStockQty(10L);
        publishSku.setSoldQty(1L);
        publishSku.setLockedQty(1L);
        publishSku.setLimitQty(3L);
        publishSku.setMinBuyQty(1L);
        publishSku.setStatus(1L);
        return publishSku;
    }

    private PrdProduct product() {
        final PrdProduct product = new PrdProduct();
        product.setId(813001L);
        product.setProductName("赣南脐橙");
        product.setFrontCategoryId(750001L);
        product.setMainImageUrl("/static/user-home/shop-home.jpg");
        product.setAuditStatus(20L);
        product.setSaleStatus(1L);
        return product;
    }

    private PrdSku sku() {
        final PrdSku sku = new PrdSku();
        sku.setId(814001L);
        sku.setSkuName("3斤装");
        sku.setSaleSpecText("3斤装/份");
        sku.setBaseUnit("份");
        sku.setStatus(1L);
        return sku;
    }

    private PrdCategory category() {
        final PrdCategory category = new PrdCategory();
        category.setId(750001L);
        category.setCategoryCode("FRONT_FRUIT");
        category.setCategoryName("水果鲜食");
        category.setSortNo(10L);
        return category;
    }

    private UsrComment comment(final Long score, final String content) {
        final UsrComment comment = new UsrComment();
        comment.setScore(score);
        comment.setContent(content);
        comment.setStatus(1L);
        return comment;
    }
}
