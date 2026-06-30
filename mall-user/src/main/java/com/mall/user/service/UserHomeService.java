package com.mall.user.service;

import com.mall.api.user.dto.UserHomeDTO;
import com.mall.api.user.dto.UserHomeAssetDTO;
import com.mall.api.user.vo.UserHomeAssetQueryVO;
import com.mall.api.user.vo.UserHomeQueryVO;

public interface UserHomeService {

    UserHomeDTO getHome(UserHomeQueryVO query);

    UserHomeAssetDTO getHomeAssets(UserHomeAssetQueryVO query);
}
