package com.mall.sale.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.sale.dto.PublishSkuDTO;
import com.mall.sale.convert.SaleConvert;
import com.mall.sale.entity.SalePublishSku;
import com.mall.sale.mapper.SalePublishSkuMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublishSkuServiceImplTest {

    @Mock
    private SalePublishSkuMapper publishSkuMapper;

    @Mock
    private SaleConvert saleConvert;

    @InjectMocks
    private PublishSkuServiceImpl publishSkuService;

    @Test
    void listPublishSkus_success() {
        final SalePublishSku publishSku = new SalePublishSku();
        publishSku.setId(756001L);
        publishSku.setSalePrice(new BigDecimal("19.9000"));
        final PublishSkuDTO publishSkuDTO = new PublishSkuDTO();
        publishSkuDTO.setId(756001L);
        publishSkuDTO.setSalePrice(new BigDecimal("19.9000"));
        when(publishSkuMapper.selectList(any())).thenReturn(List.of(publishSku));
        when(saleConvert.toPublishSkuDTO(publishSku)).thenReturn(publishSkuDTO);

        final List<PublishSkuDTO> result = publishSkuService.listPublishSkus(null);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("19.9000"), result.get(0).getSalePrice());
    }
}
