package com.mall.auth.controller;

import com.mall.api.auth.dto.LoginDTO;
import com.mall.api.auth.vo.PasswordLoginVO;
import com.mall.auth.service.AuthService;
import com.mall.common.result.Result;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/password-login")
    public Result<LoginDTO> passwordLogin(@Valid @RequestBody final PasswordLoginVO loginVO) {
        return Result.success(authService.passwordLogin(loginVO));
    }
}
