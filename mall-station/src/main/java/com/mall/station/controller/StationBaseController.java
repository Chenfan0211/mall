package com.mall.station.controller;

import com.mall.api.station.dto.LeaderDTO;
import com.mall.api.station.dto.StationDTO;
import com.mall.api.station.dto.StationLeaderDTO;
import com.mall.api.station.dto.StationLeaveDTO;
import com.mall.api.station.vo.LeaderQueryVO;
import com.mall.api.station.vo.StationLeaveApplyVO;
import com.mall.api.station.vo.StationLeaveQueryVO;
import com.mall.api.station.vo.StationQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.station.service.StationBaseService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/station/base")
public class StationBaseController {

    private final StationBaseService stationBaseService;

    public StationBaseController(final StationBaseService stationBaseService) {
        this.stationBaseService = stationBaseService;
    }

    @GetMapping("/stations")
    @PreAuthorize("hasAuthority('station:base:view')")
    public Result<PageResult<StationDTO>> pageStations(@Valid final StationQueryVO query) {
        return Result.success(stationBaseService.pageStations(query));
    }

    @GetMapping("/stations/{id}")
    @PreAuthorize("hasAuthority('station:base:view')")
    public Result<StationDTO> getStation(@PathVariable final Long id) {
        return Result.success(stationBaseService.getStation(id));
    }

    @GetMapping("/leaders")
    @PreAuthorize("hasAuthority('station:base:view')")
    public Result<PageResult<LeaderDTO>> pageLeaders(@Valid final LeaderQueryVO query) {
        return Result.success(stationBaseService.pageLeaders(query));
    }

    @GetMapping("/leaders/{id}")
    @PreAuthorize("hasAuthority('station:base:view')")
    public Result<LeaderDTO> getLeader(@PathVariable final Long id) {
        return Result.success(stationBaseService.getLeader(id));
    }

    @GetMapping("/station-leaders")
    @PreAuthorize("hasAuthority('station:base:view')")
    public Result<PageResult<StationLeaderDTO>> pageStationLeaders(@Valid final LeaderQueryVO query) {
        return Result.success(stationBaseService.pageStationLeaders(query));
    }

    @GetMapping("/station-leaders/{id}")
    @PreAuthorize("hasAuthority('station:base:view')")
    public Result<StationLeaderDTO> getStationLeader(@PathVariable final Long id) {
        return Result.success(stationBaseService.getStationLeader(id));
    }

    @GetMapping("/leaves")
    @PreAuthorize("hasAuthority('station:leave:view')")
    public Result<PageResult<StationLeaveDTO>> pageLeaves(@Valid final StationLeaveQueryVO query) {
        return Result.success(stationBaseService.pageLeaves(query));
    }

    @GetMapping("/leaves/{id}")
    @PreAuthorize("hasAuthority('station:leave:view')")
    public Result<StationLeaveDTO> getLeave(@PathVariable final Long id) {
        return Result.success(stationBaseService.getLeave(id));
    }

    @PostMapping("/leaves")
    @PreAuthorize("hasAuthority('station:leave:apply')")
    public Result<StationLeaveDTO> applyLeave(@Valid @RequestBody final StationLeaveApplyVO request) {
        return Result.success(stationBaseService.applyLeave(request));
    }

    @PostMapping("/leaves/{id}/cancel")
    @PreAuthorize("hasAuthority('station:leave:cancel')")
    public Result<StationLeaveDTO> cancelLeave(@PathVariable final Long id) {
        return Result.success(stationBaseService.cancelLeave(id));
    }
}
