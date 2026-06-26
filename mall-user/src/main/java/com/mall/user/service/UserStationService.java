package com.mall.user.service;

import com.mall.api.user.dto.UserStationDTO;
import com.mall.api.user.vo.UserStationQueryVO;
import com.mall.common.page.PageResult;

public interface UserStationService {

    PageResult<UserStationDTO> pageStations(UserStationQueryVO query);

    UserStationDTO getStation(Long id);
}
