package com.mall.station.service;

import com.mall.api.station.dto.LeaderDTO;
import com.mall.api.station.dto.StationDTO;
import com.mall.api.station.dto.StationLeaderDTO;
import com.mall.api.station.dto.StationLeaveDTO;
import com.mall.api.station.vo.LeaderQueryVO;
import com.mall.api.station.vo.StationLeaveApplyVO;
import com.mall.api.station.vo.StationLeaveQueryVO;
import com.mall.api.station.vo.StationQueryVO;
import com.mall.common.page.PageResult;

public interface StationBaseService {

    PageResult<StationDTO> pageStations(StationQueryVO query);

    StationDTO getStation(Long id);

    PageResult<LeaderDTO> pageLeaders(LeaderQueryVO query);

    LeaderDTO getLeader(Long id);

    PageResult<StationLeaderDTO> pageStationLeaders(LeaderQueryVO query);

    StationLeaderDTO getStationLeader(Long id);

    PageResult<StationLeaveDTO> pageLeaves(StationLeaveQueryVO query);

    StationLeaveDTO getLeave(Long id);

    StationLeaveDTO applyLeave(StationLeaveApplyVO request);

    StationLeaveDTO cancelLeave(Long id);
}
