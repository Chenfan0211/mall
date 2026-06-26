package com.mall.user.service;

import com.mall.api.user.dto.UserCartItemDTO;
import com.mall.api.user.vo.UserCartAddVO;
import com.mall.api.user.vo.UserCartQueryVO;
import com.mall.api.user.vo.UserCartUpdateVO;
import com.mall.common.page.PageResult;

public interface UserCartService {

    PageResult<UserCartItemDTO> pageCartItems(UserCartQueryVO query);

    UserCartItemDTO addCartItem(UserCartAddVO request);

    UserCartItemDTO updateCartItem(Long id, UserCartUpdateVO request);

    UserCartItemDTO removeCartItem(Long id, UserCartUpdateVO request);
}
