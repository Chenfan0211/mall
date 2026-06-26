package com.mall.user.controller;

import com.mall.api.user.dto.UserHomeDTO;
import com.mall.api.user.vo.UserHomeQueryVO;
import com.mall.common.result.Result;
import com.mall.user.service.UserHomeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user/home")
public class UserHomeController {

    private final UserHomeService userHomeService;

    public UserHomeController(final UserHomeService userHomeService) {
        this.userHomeService = userHomeService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('user:home:view')")
    public Result<UserHomeDTO> getHome(@Valid final UserHomeQueryVO query) {
        return Result.success(userHomeService.getHome(query));
    }
}
