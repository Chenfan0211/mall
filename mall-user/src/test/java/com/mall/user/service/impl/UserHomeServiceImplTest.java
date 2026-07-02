package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.user.dto.UserHomeAssetDTO;
import com.mall.api.user.dto.UserHomeDTO;
import com.mall.api.user.vo.UserHomeAssetQueryVO;
import com.mall.api.user.vo.UserHomeQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.PrdCategory;
import com.mall.user.entity.PrdProduct;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.entity.SysFileAsset;
import com.mall.user.entity.UsrUser;
import com.mall.user.mapper.MsgRecordMapper;
import com.mall.user.mapper.OrdCartMapper;
import com.mall.user.mapper.OrdOrderItemMapper;
import com.mall.user.mapper.OrdOrderMapper;
import com.mall.user.mapper.PrdCategoryMapper;
import com.mall.user.mapper.PrdProductMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
import com.mall.user.mapper.SysFileAssetMapper;
import com.mall.user.mapper.UsrUserMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserHomeServiceImplTest {

    @Mock
    private UsrUserMapper userMapper;

    @Mock
    private PrdCategoryMapper categoryMapper;

    @Mock
    private PrdProductMapper productMapper;

    @Mock
    private SysFileAssetMapper fileAssetMapper;

    @Mock
    private SalePublishPeriodMapper periodMapper;

    @Mock
    private SalePublishSkuMapper publishSkuMapper;

    @Mock
    private OrdCartMapper cartMapper;

    @Mock
    private OrdOrderMapper orderMapper;

    @Mock
    private OrdOrderItemMapper orderItemMapper;

    @Mock
    private MsgRecordMapper msgRecordMapper;

    @InjectMocks
    private UserHomeServiceImpl userHomeService;

    @Test
    void getHome_success() {
        final UserHomeQueryVO query = new UserHomeQueryVO();
        query.setUserId(740001L);
        final UsrUser user = new UsrUser();
        user.setId(740001L);
        user.setDefaultCityId(440100L);
        user.setDefaultStationId(720001L);
        user.setStatus(1L);
        final OrdOrder order = new OrdOrder();
        order.setId(761005L);

        when(userMapper.selectById(740001L)).thenReturn(user);
        when(periodMapper.selectList(any())).thenReturn(List.of(onlinePeriod()));
        when(publishSkuMapper.selectCount(any())).thenReturn(2L);
        when(cartMapper.selectCount(any())).thenReturn(2L);
        when(orderMapper.selectCount(any())).thenReturn(1L);
        when(orderMapper.selectList(any())).thenReturn(List.of(order));
        when(orderItemMapper.selectCount(any())).thenReturn(1L);
        when(msgRecordMapper.selectCount(any())).thenReturn(1L);

        final UserHomeDTO result = userHomeService.getHome(query);

        assertEquals(440100L, result.getCityId());
        assertEquals(720001L, result.getStationId());
        assertEquals(2L, result.getOnlineProductCount());
        assertEquals(1L, result.getUnreadMessageCount());
    }

    @Test
    void getHome_disabledUser_throwConflict() {
        final UserHomeQueryVO query = new UserHomeQueryVO();
        query.setUserId(740002L);
        final UsrUser user = new UsrUser();
        user.setId(740002L);
        user.setStatus(2L);
        when(userMapper.selectById(740002L)).thenReturn(user);

        assertThrows(BusinessException.class, () -> userHomeService.getHome(query));
    }

    @Test
    void getHome_missingUser_returnEmptySummary() {
        final UserHomeQueryVO query = new UserHomeQueryVO();
        query.setUserId(999001L);
        query.setCityId(440100L);
        query.setStationId(720001L);
        when(userMapper.selectById(999001L)).thenReturn(null);
        when(periodMapper.selectList(any())).thenReturn(List.of());

        final UserHomeDTO result = userHomeService.getHome(query);

        assertEquals(999001L, result.getUserId());
        assertEquals(440100L, result.getCityId());
        assertEquals(720001L, result.getStationId());
        assertEquals(0L, result.getOnlineProductCount());
        assertEquals(0L, result.getCartItemCount());
        assertEquals(0L, result.getWaitPayOrderCount());
        assertEquals(0L, result.getWaitPickupOrderCount());
        assertEquals(0L, result.getUnreadMessageCount());
    }

    @Test
    void getHomeAssets_filtersCategoriesAndReturnsUploadedAssets() {
        final PrdCategory category = category();
        final SysFileAsset icon = asset("USER_CATEGORY_ICON", "FRONT_FRUIT", "/static/user-home/shop-home.jpg");
        final SysFileAsset banner = asset("USER_HOME_BANNER", "HOME_MAIN", "/static/user-home/shop-home.jpg");
        final SysFileAsset promo = asset("USER_HOME_PROMO", "ORCHARD_DIRECT", "/static/user-home/shop-seafood.jpg");
        final UserHomeAssetQueryVO query = new UserHomeAssetQueryVO();
        query.setCityId(440100L);
        query.setStationId(720001L);

        when(periodMapper.selectList(any())).thenReturn(List.of(onlinePeriod()));
        when(publishSkuMapper.selectList(any())).thenReturn(List.of(publishSku()));
        when(productMapper.selectBatchIds(any())).thenReturn(List.of(product()));
        when(categoryMapper.selectList(any())).thenReturn(List.of(category));
        when(fileAssetMapper.selectList(any())).thenReturn(List.of(icon), List.of(banner), List.of(promo));

        final UserHomeAssetDTO result = userHomeService.getHomeAssets(query);

        assertEquals(1, result.getCategories().size());
        assertEquals("/static/user-home/shop-home.jpg", result.getCategories().get(0).getImageUrl());
        assertEquals("HOME_MAIN", result.getBanners().get(0).getActivityCode());
        assertEquals("/pages/activity/index?activityCode=HOME_MAIN", result.getBanners().get(0).getLinkUrl());
        assertEquals("ORCHARD_DIRECT", result.getPromos().get(0).getActivityCode());
    }

    @Test
    void getHomeAssets_noOnlineCategory_hideCategories() {
        when(periodMapper.selectList(any())).thenReturn(List.of());
        when(fileAssetMapper.selectList(any())).thenReturn(List.of(), List.of(), List.of());

        final UserHomeAssetDTO result = userHomeService.getHomeAssets(new UserHomeAssetQueryVO());

        assertEquals(0, result.getCategories().size());
        assertEquals(0, result.getBanners().size());
        assertEquals(0, result.getPromos().size());
    }

    private SalePublishPeriod onlinePeriod() {
        final SalePublishPeriod period = new SalePublishPeriod();
        period.setId(810900L);
        period.setCityId(440100L);
        period.setSaleStartTime(LocalDateTime.now().minusHours(1L));
        period.setActualCutoffTime(LocalDateTime.now().plusHours(1L));
        period.setStatus(20L);
        return period;
    }

    private SalePublishSku publishSku() {
        final SalePublishSku publishSku = new SalePublishSku();
        publishSku.setProductId(813001L);
        publishSku.setPlannedStockQty(10L);
        publishSku.setSoldQty(1L);
        publishSku.setLockedQty(0L);
        publishSku.setStatus(1L);
        return publishSku;
    }

    private PrdProduct product() {
        final PrdProduct product = new PrdProduct();
        product.setId(813001L);
        product.setFrontCategoryId(750001L);
        product.setAuditStatus(20L);
        product.setSaleStatus(1L);
        return product;
    }

    private PrdCategory category() {
        final PrdCategory category = new PrdCategory();
        category.setId(750001L);
        category.setCategoryCode("FRONT_FRUIT");
        category.setCategoryName("水果鲜食");
        category.setSortNo(10L);
        return category;
    }

    private SysFileAsset asset(final String bizType, final String bizNo, final String url) {
        final SysFileAsset asset = new SysFileAsset();
        asset.setBizType(bizType);
        asset.setBizNo(bizNo);
        asset.setFileUrl(url);
        asset.setStatus(1L);
        return asset;
    }
}
