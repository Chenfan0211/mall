package com.mall.user.controller;

import com.mall.api.user.dto.UserStationDTO;
import com.mall.api.user.vo.UserStationQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserStationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user/stations")
public class UserStationController {

    private final UserStationService userStationService;

    public UserStationController(final UserStationService userStationService) {
        this.userStationService = userStationService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:station:view')")
    public Result<PageResult<UserStationDTO>> pageStations(@Valid final UserStationQueryVO query) {
        return Result.success(userStationService.pageStations(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:station:view')")
    public Result<UserStationDTO> getStation(@PathVariable final Long id) {
        return Result.success(userStationService.getStation(id));
    }
}
