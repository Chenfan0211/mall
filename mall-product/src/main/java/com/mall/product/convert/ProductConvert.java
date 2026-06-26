package com.mall.product.convert;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.product.dto.SkuDTO;
import com.mall.product.entity.PrdCategory;
import com.mall.product.entity.PrdProduct;
import com.mall.product.entity.PrdSku;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductConvert {

    ProductDTO toProductDTO(PrdProduct entity);

    CategoryDTO toCategoryDTO(PrdCategory entity);

    SkuDTO toSkuDTO(PrdSku entity);
}
