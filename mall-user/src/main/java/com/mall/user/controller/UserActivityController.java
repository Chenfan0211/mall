package com.mall.user.controller;

import com.mall.api.user.dto.UserProductCardDTO;
import com.mall.api.user.vo.UserProductQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserProductService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user/activities")
public class UserActivityController {

    private final UserProductService userProductService;

    public UserActivityController(final UserProductService userProductService) {
        this.userProductService = userProductService;
    }

    @GetMapping("/{activityCode}/products")
    public Result<PageResult<UserProductCardDTO>> pageActivityProducts(
            @PathVariable final String activityCode,
            @Valid final UserProductQueryVO query) {
        return Result.success(userProductService.pageActivityProducts(activityCode, query));
    }
}
