package com.mall.auth.service;

import com.mall.api.auth.dto.LoginDTO;
import com.mall.api.auth.vo.PasswordLoginVO;

public interface AuthService {

    LoginDTO passwordLogin(PasswordLoginVO loginVO);
}
