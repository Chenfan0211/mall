package com.mall.wms.service;

import com.mall.api.wms.dto.DriverDTO;
import com.mall.api.wms.dto.LineDTO;
import com.mall.api.wms.dto.LineStationDTO;
import com.mall.api.wms.dto.LocationDTO;
import com.mall.api.wms.dto.WarehouseDTO;
import com.mall.api.wms.dto.ZoneDTO;
import com.mall.api.wms.vo.WmsBaseQueryVO;
import com.mall.common.page.PageResult;

public interface WmsBaseService {

    PageResult<WarehouseDTO> pageWarehouses(WmsBaseQueryVO query);

    WarehouseDTO getWarehouse(Long id);

    PageResult<ZoneDTO> pageZones(WmsBaseQueryVO query);

    ZoneDTO getZone(Long id);

    PageResult<LocationDTO> pageLocations(WmsBaseQueryVO query);

    LocationDTO getLocation(Long id);

    PageResult<LineDTO> pageLines(WmsBaseQueryVO query);

    LineDTO getLine(Long id);

    PageResult<LineStationDTO> pageLineStations(WmsBaseQueryVO query);

    LineStationDTO getLineStation(Long id);

    PageResult<DriverDTO> pageDrivers(WmsBaseQueryVO query);

    DriverDTO getDriver(Long id);
}
