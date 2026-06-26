package com.mall.wms.controller;

import com.mall.api.wms.dto.DriverDTO;
import com.mall.api.wms.dto.LineDTO;
import com.mall.api.wms.dto.LineStationDTO;
import com.mall.api.wms.dto.LocationDTO;
import com.mall.api.wms.dto.WarehouseDTO;
import com.mall.api.wms.dto.ZoneDTO;
import com.mall.api.wms.vo.WmsBaseQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.wms.service.WmsBaseService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/wms/base")
public class WmsBaseController {

    private final WmsBaseService wmsBaseService;

    public WmsBaseController(final WmsBaseService wmsBaseService) {
        this.wmsBaseService = wmsBaseService;
    }

    @GetMapping("/warehouses")
    @PreAuthorize("hasAuthority('wms:warehouse:view')")
    public Result<PageResult<WarehouseDTO>> pageWarehouses(@Valid final WmsBaseQueryVO query) {
        return Result.success(wmsBaseService.pageWarehouses(query));
    }

    @GetMapping("/warehouses/{id}")
    @PreAuthorize("hasAuthority('wms:warehouse:view')")
    public Result<WarehouseDTO> getWarehouse(@PathVariable final Long id) {
        return Result.success(wmsBaseService.getWarehouse(id));
    }

    @GetMapping("/zones")
    @PreAuthorize("hasAuthority('wms:zone:view')")
    public Result<PageResult<ZoneDTO>> pageZones(@Valid final WmsBaseQueryVO query) {
        return Result.success(wmsBaseService.pageZones(query));
    }

    @GetMapping("/zones/{id}")
    @PreAuthorize("hasAuthority('wms:zone:view')")
    public Result<ZoneDTO> getZone(@PathVariable final Long id) {
        return Result.success(wmsBaseService.getZone(id));
    }

    @GetMapping("/locations")
    @PreAuthorize("hasAuthority('wms:location:view')")
    public Result<PageResult<LocationDTO>> pageLocations(@Valid final WmsBaseQueryVO query) {
        return Result.success(wmsBaseService.pageLocations(query));
    }

    @GetMapping("/locations/{id}")
    @PreAuthorize("hasAuthority('wms:location:view')")
    public Result<LocationDTO> getLocation(@PathVariable final Long id) {
        return Result.success(wmsBaseService.getLocation(id));
    }

    @GetMapping("/lines")
    @PreAuthorize("hasAuthority('wms:line:view')")
    public Result<PageResult<LineDTO>> pageLines(@Valid final WmsBaseQueryVO query) {
        return Result.success(wmsBaseService.pageLines(query));
    }

    @GetMapping("/lines/{id}")
    @PreAuthorize("hasAuthority('wms:line:view')")
    public Result<LineDTO> getLine(@PathVariable final Long id) {
        return Result.success(wmsBaseService.getLine(id));
    }

    @GetMapping("/line-stations")
    @PreAuthorize("hasAuthority('wms:line-station:view')")
    public Result<PageResult<LineStationDTO>> pageLineStations(@Valid final WmsBaseQueryVO query) {
        return Result.success(wmsBaseService.pageLineStations(query));
    }

    @GetMapping("/line-stations/{id}")
    @PreAuthorize("hasAuthority('wms:line-station:view')")
    public Result<LineStationDTO> getLineStation(@PathVariable final Long id) {
        return Result.success(wmsBaseService.getLineStation(id));
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasAuthority('wms:driver:view')")
    public Result<PageResult<DriverDTO>> pageDrivers(@Valid final WmsBaseQueryVO query) {
        return Result.success(wmsBaseService.pageDrivers(query));
    }

    @GetMapping("/drivers/{id}")
    @PreAuthorize("hasAuthority('wms:driver:view')")
    public Result<DriverDTO> getDriver(@PathVariable final Long id) {
        return Result.success(wmsBaseService.getDriver(id));
    }
}
