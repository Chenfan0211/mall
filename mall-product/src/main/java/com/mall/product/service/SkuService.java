package com.mall.product.service;

import com.mall.api.product.dto.SkuDTO;
import com.mall.api.product.dto.StockSpecDTO;
import com.mall.api.product.vo.SkuQueryVO;
import java.util.List;

public interface SkuService {

    List<SkuDTO> listSkus(SkuQueryVO query);

    SkuDTO getSku(Long id);

    List<StockSpecDTO> listStockSpecs(Long skuId);
}
