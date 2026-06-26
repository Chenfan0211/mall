package com.mall.product.service;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.vo.CategoryQueryVO;
import java.util.List;

public interface CategoryService {

    List<CategoryDTO> listCategories(CategoryQueryVO query);
}
