package com.mall.user.service;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.dto.UserProductDetailDTO;
import com.mall.api.user.vo.UserProductQueryVO;
import com.mall.common.page.PageResult;

public interface UserProductService {

    PageResult<CategoryDTO> pageCategories(UserProductQueryVO query);

    PageResult<UserProductCardDTO> pageProducts(UserProductQueryVO query);

    UserProductDetailDTO getProductDetail(Long id, UserProductQueryVO query);

    PageResult<UserCommentDTO> pageComments(UserProductQueryVO query);
}
