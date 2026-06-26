package com.mall.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.api.auth.dto.LoginDTO;
import com.mall.api.auth.vo.PasswordLoginVO;
import com.mall.api.system.dto.AccountDTO;
import com.mall.auth.service.AuthService;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.common.web.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void passwordLogin_success() throws Exception {
        final PasswordLoginVO loginVO = new PasswordLoginVO();
        loginVO.setUsername("test_platform_admin");
        loginVO.setPassword("Test@123456");
        loginVO.setPortalCode("admin");
        final AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(700001L);
        accountDTO.setUsername("test_platform_admin");
        final LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccessToken("token");
        loginDTO.setTokenType("Bearer");
        loginDTO.setExpiresIn(7200L);
        loginDTO.setAccount(accountDTO);
        loginDTO.setAuthorities(List.of("operation:todo:view"));

        when(authService.passwordLogin(any(PasswordLoginVO.class))).thenReturn(loginDTO);

        mockMvc.perform(post("/api/auth/password-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.authorities[0]").value("operation:todo:view"))
                .andExpect(jsonPath("$.data.account.username").value("test_platform_admin"));
    }

    @Test
    void passwordLogin_missingUsername_returnParamError() throws Exception {
        final PasswordLoginVO loginVO = new PasswordLoginVO();
        loginVO.setPassword("Test@123456");
        loginVO.setPortalCode("admin");

        mockMvc.perform(post("/api/auth/password-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("username:账号不能为空"));
    }

    @Test
    void passwordLogin_passwordMismatch_returnUnauthorized() throws Exception {
        final PasswordLoginVO loginVO = new PasswordLoginVO();
        loginVO.setUsername("test_platform_admin");
        loginVO.setPassword("wrong");
        loginVO.setPortalCode("admin");

        when(authService.passwordLogin(any(PasswordLoginVO.class)))
                .thenThrow(new BusinessException(CommonErrorCode.UNAUTHORIZED, "账号或密码错误"));

        mockMvc.perform(post("/api/auth/password-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("账号或密码错误"));
    }
}
