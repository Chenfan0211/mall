package com.mall.product.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.product.dto.SkuDTO;
import com.mall.api.product.dto.StockSpecDTO;
import com.mall.common.exception.BusinessException;
import com.mall.product.convert.ProductConvert;
import com.mall.product.entity.PrdSku;
import com.mall.product.mapper.PrdSkuMapper;
import com.mall.product.mapper.PrdStockSpecMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkuServiceImplTest {

    @Mock
    private PrdSkuMapper skuMapper;

    @Mock
    private PrdStockSpecMapper stockSpecMapper;

    @Mock
    private ProductConvert productConvert;

    @InjectMocks
    private SkuServiceImpl skuService;

    @Test
    void listSkus_success() {
        final PrdSku sku = new PrdSku();
        sku.setId(752001L);
        sku.setSkuName("苹果1kg装");
        final SkuDTO skuDTO = new SkuDTO();
        skuDTO.setId(752001L);
        skuDTO.setSkuName("苹果1kg装");
        when(skuMapper.selectList(any())).thenReturn(List.of(sku));
        when(productConvert.toSkuDTO(sku)).thenReturn(skuDTO);

        final List<SkuDTO> result = skuService.listSkus(null);

        assertEquals(1, result.size());
        assertEquals("苹果1kg装", result.get(0).getSkuName());
    }

    @Test
    void getSku_notFound_throwNotFound() {
        when(skuMapper.selectById(752999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> skuService.getSku(752999L));
    }

    @Test
    void listStockSpecs_success() {
        final StockSpecDTO stockSpecDTO = new StockSpecDTO();
        stockSpecDTO.setId(753001L);
        stockSpecDTO.setSpecName("按份");
        when(stockSpecMapper.selectBySkuId(752001L)).thenReturn(List.of(stockSpecDTO));

        final List<StockSpecDTO> result = skuService.listStockSpecs(752001L);

        assertEquals(1, result.size());
        assertEquals("按份", result.get(0).getSpecName());
    }
}
