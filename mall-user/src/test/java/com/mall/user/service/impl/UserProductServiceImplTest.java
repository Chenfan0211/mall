package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.api.user.vo.UserProductQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.PrdCategory;
import com.mall.user.entity.PrdProduct;
import com.mall.user.entity.PrdSku;
import com.mall.user.entity.SalePublishPeriod;
import com.mall.user.entity.SalePublishSku;
import com.mall.user.entity.UsrBrowseHistory;
import com.mall.user.entity.UsrComment;
import com.mall.user.entity.UsrUserFavorite;
import com.mall.user.mapper.PrdCategoryMapper;
import com.mall.user.mapper.PrdProductMapper;
import com.mall.user.mapper.PrdSkuMapper;
import com.mall.user.mapper.SalePublishPeriodMapper;
import com.mall.user.mapper.SalePublishSkuMapper;
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
    private UserConvert userConvert;

    @InjectMocks
    private UserProductServiceImpl userProductService;

    @Test
    void pageCategories_success() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setKeyword("水果");
        final PrdCategory category = new PrdCategory();
        category.setId(750001L);
        category.setCategoryName("水果鲜食");
        final CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(750001L);
        categoryDTO.setCategoryName("水果鲜食");
        final Page<PrdCategory> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(category));
        when(categoryMapper.selectPage(any(), any())).thenReturn(page);
        when(userConvert.toCategoryDTO(category)).thenReturn(categoryDTO);

        final PageResult<CategoryDTO> result = userProductService.pageCategories(query);

        assertEquals(1L, result.getTotal());
        assertEquals("水果鲜食", result.getList().get(0).getCategoryName());
    }

    @Test
    void getProductDetail_success() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setUserId(740001L);
        query.setStationId(720001L);
        final PrdProduct product = new PrdProduct();
        product.setId(751001L);
        product.setProductName("测试红富士苹果");
        product.setAuditStatus(20L);
        product.setSaleStatus(1L);
        final SalePublishPeriod period = new SalePublishPeriod();
        period.setId(755002L);
        period.setDeliveryDate(LocalDate.now());
        period.setSaleStartTime(LocalDateTime.now().minusHours(1L));
        period.setActualCutoffTime(LocalDateTime.now().plusHours(1L));
        final SalePublishSku publishSku = new SalePublishSku();
        publishSku.setId(756001L);
        publishSku.setPeriodId(755002L);
        publishSku.setProductId(751001L);
        publishSku.setSkuId(752001L);
        publishSku.setSalePrice(new BigDecimal("19.90"));
        publishSku.setPlannedStockQty(10L);
        publishSku.setSoldQty(2L);
        publishSku.setLockedQty(1L);
        publishSku.setLimitQty(3L);
        publishSku.setMinBuyQty(1L);
        publishSku.setStatus(1L);
        final PrdSku sku = new PrdSku();
        sku.setId(752001L);
        sku.setSkuName("2.5kg装");
        sku.setSaleSpecText("2.5kg");
        sku.setBaseUnit("kg");
        sku.setStatus(1L);
        final ProductDTO productDTO = new ProductDTO();
        productDTO.setId(751001L);
        productDTO.setProductName("测试红富士苹果");
        when(productMapper.selectById(751001L)).thenReturn(product);
        when(periodMapper.selectList(any())).thenReturn(List.of(period));
        when(publishSkuMapper.selectList(any())).thenReturn(List.of(publishSku));
        when(skuMapper.selectById(752001L)).thenReturn(sku);
        when(favoriteMapper.selectCount(any())).thenReturn(1L);
        when(commentMapper.selectCount(any())).thenReturn(2L);
        when(userConvert.toProductDTO(product)).thenReturn(productDTO);

        final UserProductDetailDTO result = userProductService.getProductDetail(751001L, query);

        assertEquals(1L, result.getFavoriteFlag());
        assertEquals(2L, result.getCommentCount());
        assertEquals("测试红富士苹果", result.getProduct().getProductName());
    }

    @Test
    void getProductDetail_notFound_throwNotFound() {
        when(productMapper.selectById(751999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userProductService.getProductDetail(751999L, new UserProductQueryVO()));
    }

    @Test
    void pageComments_success() {
        final UserProductQueryVO query = new UserProductQueryVO();
        query.setProductId(751001L);
        final UsrComment comment = new UsrComment();
        comment.setId(772001L);
        comment.setContent("新鲜好吃");
        final UserCommentDTO commentDTO = new UserCommentDTO();
        commentDTO.setId(772001L);
        commentDTO.setContent("新鲜好吃");
        final Page<UsrComment> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(comment));
        when(commentMapper.selectPage(any(), any())).thenReturn(page);
        when(userConvert.toCommentDTO(comment)).thenReturn(commentDTO);

        final PageResult<UserCommentDTO> result = userProductService.pageComments(query);

        assertEquals(1L, result.getTotal());
        assertEquals("新鲜好吃", result.getList().get(0).getContent());
    }
}
