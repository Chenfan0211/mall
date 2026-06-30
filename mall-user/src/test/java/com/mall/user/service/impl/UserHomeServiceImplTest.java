package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.user.dto.UserHomeDTO;
import com.mall.api.user.dto.UserHomeAssetDTO;
import com.mall.api.user.vo.UserHomeAssetQueryVO;
import com.mall.api.user.vo.UserHomeQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.PrdCategory;
import com.mall.user.entity.SalePublishPeriod;
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
        final SalePublishPeriod period = new SalePublishPeriod();
        period.setId(755002L);
        period.setSaleStartTime(LocalDateTime.now().minusHours(1L));
        period.setActualCutoffTime(LocalDateTime.now().plusHours(1L));
        final OrdOrder order = new OrdOrder();
        order.setId(761005L);
        when(userMapper.selectById(740001L)).thenReturn(user);
        when(periodMapper.selectList(any())).thenReturn(List.of(period));
        when(publishSkuMapper.selectCount(any())).thenReturn(2L);
        when(cartMapper.selectCount(any())).thenReturn(2L);
        when(orderMapper.selectCount(any())).thenReturn(1L);
        when(orderMapper.selectList(any())).thenReturn(List.of(order));
        when(orderItemMapper.selectCount(any())).thenReturn(1L);
        when(msgRecordMapper.selectCount(any())).thenReturn(1L);

        final UserHomeDTO result = userHomeService.getHome(query);

        assertEquals(440100L, result.getCityId());
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
    void getHomeAssets_success() {
        final PrdCategory category = new PrdCategory();
        category.setId(750001L);
        category.setCategoryCode("FRONT_FRUIT");
        category.setCategoryName("水果鲜食");
        category.setSortNo(10L);
        final SysFileAsset icon = new SysFileAsset();
        icon.setBizType("USER_CATEGORY_ICON");
        icon.setBizNo("FRONT_FRUIT");
        icon.setFileUrl("https://cdn.example.com/fruit.png");
        icon.setStatus(1L);
        final SysFileAsset banner = new SysFileAsset();
        banner.setBizType("USER_HOME_BANNER");
        banner.setBizNo("HOME_MAIN");
        banner.setFileUrl("https://cdn.example.com/banner.png");
        banner.setStatus(1L);
        final SysFileAsset promo = new SysFileAsset();
        promo.setBizType("USER_HOME_PROMO");
        promo.setBizNo("ORCHARD_DIRECT");
        promo.setFileUrl("https://cdn.example.com/promo.png");
        promo.setStatus(1L);
        when(categoryMapper.selectList(any())).thenReturn(List.of(category));
        when(fileAssetMapper.selectList(any())).thenReturn(List.of(icon), List.of(banner), List.of(promo));

        final UserHomeAssetDTO result = userHomeService.getHomeAssets(new UserHomeAssetQueryVO());

        assertEquals("https://cdn.example.com/fruit.png", result.getCategories().get(0).getImageUrl());
        assertEquals("https://cdn.example.com/banner.png", result.getBanners().get(0).getImageUrl());
        assertEquals("HOME_MAIN", result.getBanners().get(0).getActivityCode());
        assertEquals("/pages/activity/index?activityCode=HOME_MAIN", result.getBanners().get(0).getLinkUrl());
        assertEquals("https://cdn.example.com/promo.png", result.getPromos().get(0).getImageUrl());
        assertEquals("ORCHARD_DIRECT", result.getPromos().get(0).getActivityCode());
        assertEquals("/pages/activity/index?activityCode=ORCHARD_DIRECT", result.getPromos().get(0).getLinkUrl());
    }

    @Test
    void getHomeAssets_noUploadedOperationImages_hideBannersAndPromos() {
        final PrdCategory category = new PrdCategory();
        category.setId(750001L);
        category.setCategoryCode("FRONT_FRUIT");
        category.setCategoryName("水果鲜食");
        when(categoryMapper.selectList(any())).thenReturn(List.of(category));
        when(fileAssetMapper.selectList(any())).thenReturn(List.of(), List.of(), List.of());

        final UserHomeAssetDTO result = userHomeService.getHomeAssets(new UserHomeAssetQueryVO());

        assertEquals(0, result.getBanners().size());
        assertEquals(0, result.getPromos().size());
    }
}
