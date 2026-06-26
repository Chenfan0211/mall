package com.mall.product.service;

import com.mall.api.product.dto.ProductDTO;
import com.mall.api.product.vo.ProductQueryVO;
import com.mall.common.page.PageResult;

public interface ProductService {

    PageResult<ProductDTO> pageProducts(ProductQueryVO query);

    ProductDTO getProduct(Long id);
}
