package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mall.api.user.dto.UserCartItemDTO;
import com.mall.api.user.vo.UserCartAddVO;
import com.mall.api.user.vo.UserCartUpdateVO;
import com.mall.common.exception.BusinessException;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCartServiceImplTest {

    @Mock
    private OrdCartMapper cartMapper;

    @Mock
    private SalePublishPeriodMapper periodMapper;

    @Mock
    private SalePublishSkuMapper publishSkuMapper;

    @Mock
    private PrdProductMapper productMapper;

    @Mock
    private PrdSkuMapper skuMapper;

    @Mock
    private UserConvert userConvert;

    @InjectMocks
    private UserCartServiceImpl userCartService;

    @Test
    void addCartItem_existingItem_accumulateQty() {
        final UserCartAddVO request = buildAddRequest();
        final SalePublishSku publishSku = new SalePublishSku();
        publishSku.setPeriodId(755002L);
        publishSku.setSkuId(752001L);
        publishSku.setPlannedStockQty(100L);
        publishSku.setSoldQty(10L);
        publishSku.setLockedQty(0L);
        publishSku.setSalePrice(new BigDecimal("19.9000"));
        final OrdCart cart = new OrdCart();
        cart.setId(760001L);
        cart.setUserId(740001L);
        cart.setPeriodId(755002L);
        cart.setProductId(751001L);
        cart.setSkuId(752001L);
        cart.setQty(2L);
        final UserCartItemDTO dto = new UserCartItemDTO();
        dto.setId(760001L);
        dto.setQty(3L);
        final SalePublishPeriod period = new SalePublishPeriod();
        period.setId(755002L);
        period.setStatus(20L);
        period.setSaleStartTime(LocalDateTime.now().minusHours(1L));
        period.setActualCutoffTime(LocalDateTime.now().plusHours(1L));
        when(periodMapper.selectById(755002L)).thenReturn(period);
        when(publishSkuMapper.selectOne(any())).thenReturn(publishSku);
        when(cartMapper.selectOne(any())).thenReturn(cart);
        when(cartMapper.updateById(cart)).thenReturn(1);
        final PrdProduct product = new PrdProduct();
        product.setId(751001L);
        product.setProductName("测试商品");
        final PrdSku sku = new PrdSku();
        sku.setId(752001L);
        sku.setSkuName("测试SKU");
        when(productMapper.selectBatchIds(any())).thenReturn(List.of(product));
        when(skuMapper.selectBatchIds(any())).thenReturn(List.of(sku));
        when(publishSkuMapper.selectList(any())).thenReturn(List.of(publishSku));
        when(userConvert.toCartItemDTO(cart)).thenReturn(dto);

        final UserCartItemDTO result = userCartService.addCartItem(request);

        assertEquals(3L, cart.getQty());
        assertEquals(3L, result.getQty());
        verify(cartMapper).updateById(cart);
    }

    @Test
    void removeCartItem_otherUser_throwForbidden() {
        final UserCartUpdateVO request = new UserCartUpdateVO();
        request.setUserId(740002L);
        final OrdCart cart = new OrdCart();
        cart.setId(760001L);
        cart.setUserId(740001L);
        when(cartMapper.selectById(760001L)).thenReturn(cart);

        assertThrows(BusinessException.class, () -> userCartService.removeCartItem(760001L, request));
    }

    private UserCartAddVO buildAddRequest() {
        final UserCartAddVO request = new UserCartAddVO();
        request.setUserId(740001L);
        request.setCityId(440100L);
        request.setStationId(720001L);
        request.setPeriodId(755002L);
        request.setProductId(751001L);
        request.setSkuId(752001L);
        request.setQty(1L);
        return request;
    }
}
