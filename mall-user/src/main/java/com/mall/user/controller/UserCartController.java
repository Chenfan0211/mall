package com.mall.user.controller;

import com.mall.api.user.dto.UserCartItemDTO;
import com.mall.api.user.vo.UserCartAddVO;
import com.mall.api.user.vo.UserCartQueryVO;
import com.mall.api.user.vo.UserCartUpdateVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserCartService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user/cart")
public class UserCartController {

    private final UserCartService userCartService;

    public UserCartController(final UserCartService userCartService) {
        this.userCartService = userCartService;
    }

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('user:cart:view')")
    public Result<PageResult<UserCartItemDTO>> pageCartItems(@Valid final UserCartQueryVO query) {
        return Result.success(userCartService.pageCartItems(query));
    }

    @PostMapping("/items")
    @PreAuthorize("hasAuthority('user:cart:add')")
    public Result<UserCartItemDTO> addCartItem(@Valid @RequestBody final UserCartAddVO request) {
        return Result.success(userCartService.addCartItem(request));
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAuthority('user:cart:update')")
    public Result<UserCartItemDTO> updateCartItem(
            @PathVariable final Long id,
            @Valid @RequestBody final UserCartUpdateVO request) {
        return Result.success(userCartService.updateCartItem(id, request));
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasAuthority('user:cart:remove')")
    public Result<UserCartItemDTO> removeCartItem(
            @PathVariable final Long id,
            @Valid @RequestBody final UserCartUpdateVO request) {
        return Result.success(userCartService.removeCartItem(id, request));
    }
}
