package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.user.dto.UserStationDTO;
import com.mall.api.user.vo.UserStationQueryVO;
import com.mall.common.page.PageResult;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.UsrStation;
import com.mall.user.mapper.UsrStationMapper;
import com.mall.user.service.UserStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserStationServiceImpl extends UserReadSupport implements UserStationService {

    private final UsrStationMapper stationMapper;
    private final UserConvert userConvert;

    public UserStationServiceImpl(final UsrStationMapper stationMapper, final UserConvert userConvert) {
        this.stationMapper = stationMapper;
        this.userConvert = userConvert;
    }

    @Override
    public PageResult<UserStationDTO> pageStations(final UserStationQueryVO query) {
        final UserStationQueryVO safeQuery = query == null ? new UserStationQueryVO() : query;
        final LambdaQueryWrapper<UsrStation> wrapper = new LambdaQueryWrapper<UsrStation>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(UsrStation::getStationNo, safeQuery.getKeyword())
                        .or()
                        .like(UsrStation::getStationName, safeQuery.getKeyword())
                        .or()
                        .like(UsrStation::getAddress, safeQuery.getKeyword()))
                .eq(safeQuery.getCityId() != null, UsrStation::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStatus() != null, UsrStation::getStatus, safeQuery.getStatus())
                .orderByDesc(UsrStation::getId);
        return toPage(safeQuery, stationMapper, wrapper, userConvert::toStationDTO);
    }

    @Override
    public UserStationDTO getStation(final Long id) {
        return getById(id, stationMapper, userConvert::toStationDTO, "自提点");
    }
}
