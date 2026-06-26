package com.mall.product.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.product.vo.ProductQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.product.convert.ProductConvert;
import com.mall.product.entity.PrdProduct;
import com.mall.product.mapper.PrdProductMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private PrdProductMapper productMapper;

    @Mock
    private ProductConvert productConvert;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void pageProducts_success() {
        final ProductQueryVO query = new ProductQueryVO();
        query.setKeyword("苹果");
        final PrdProduct product = new PrdProduct();
        product.setId(751001L);
        product.setProductName("测试红富士苹果");
        final ProductDTO productDTO = new ProductDTO();
        productDTO.setId(751001L);
        productDTO.setProductName("测试红富士苹果");
        final Page<PrdProduct> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(product));
        when(productMapper.selectPage(any(), any())).thenReturn(page);
        when(productConvert.toProductDTO(product)).thenReturn(productDTO);

        final PageResult<ProductDTO> result = productService.pageProducts(query);

        assertEquals(1L, result.getTotal());
        assertEquals("测试红富士苹果", result.getList().get(0).getProductName());
    }

    @Test
    void getProduct_notFound_throwNotFound() {
        when(productMapper.selectById(751999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> productService.getProduct(751999L));
    }
}
