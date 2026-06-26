package com.mall.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.wms.dto.DriverDTO;
import com.mall.api.wms.dto.LineDTO;
import com.mall.api.wms.dto.LineStationDTO;
import com.mall.api.wms.dto.LocationDTO;
import com.mall.api.wms.dto.WarehouseDTO;
import com.mall.api.wms.dto.ZoneDTO;
import com.mall.api.wms.vo.WmsBaseQueryVO;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsDriver;
import com.mall.wms.entity.WmsLine;
import com.mall.wms.entity.WmsLineStation;
import com.mall.wms.entity.WmsLocation;
import com.mall.wms.entity.WmsWarehouse;
import com.mall.wms.entity.WmsZone;
import com.mall.wms.mapper.WmsDriverMapper;
import com.mall.wms.mapper.WmsLineMapper;
import com.mall.wms.mapper.WmsLineStationMapper;
import com.mall.wms.mapper.WmsLocationMapper;
import com.mall.wms.mapper.WmsWarehouseMapper;
import com.mall.wms.mapper.WmsZoneMapper;
import com.mall.wms.service.WmsBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class WmsBaseServiceImpl extends WmsReadSupport implements WmsBaseService {

    private final WmsWarehouseMapper warehouseMapper;
    private final WmsZoneMapper zoneMapper;
    private final WmsLocationMapper locationMapper;
    private final WmsLineMapper lineMapper;
    private final WmsLineStationMapper lineStationMapper;
    private final WmsDriverMapper driverMapper;
    private final WmsConvert wmsConvert;

    public WmsBaseServiceImpl(
            final WmsWarehouseMapper warehouseMapper,
            final WmsZoneMapper zoneMapper,
            final WmsLocationMapper locationMapper,
            final WmsLineMapper lineMapper,
            final WmsLineStationMapper lineStationMapper,
            final WmsDriverMapper driverMapper,
            final WmsConvert wmsConvert) {
        this.warehouseMapper = warehouseMapper;
        this.zoneMapper = zoneMapper;
        this.locationMapper = locationMapper;
        this.lineMapper = lineMapper;
        this.lineStationMapper = lineStationMapper;
        this.driverMapper = driverMapper;
        this.wmsConvert = wmsConvert;
    }

    @Override
    public PageResult<WarehouseDTO> pageWarehouses(final WmsBaseQueryVO query) {
        final WmsBaseQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsWarehouse> wrapper = new LambdaQueryWrapper<WmsWarehouse>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsWarehouse::getWarehouseCode, safeQuery.getKeyword())
                        .or()
                        .like(WmsWarehouse::getWarehouseName, safeQuery.getKeyword()))
                .eq(safeQuery.getWarehouseId() != null, WmsWarehouse::getId, safeQuery.getWarehouseId())
                .eq(safeQuery.getCityId() != null, WmsWarehouse::getCityId, safeQuery.getCityId())
                .eq(safeQuery.getStatus() != null, WmsWarehouse::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsWarehouse::getId);
        return toPage(safeQuery, warehouseMapper, wrapper, wmsConvert::toWarehouseDTO);
    }

    @Override
    public WarehouseDTO getWarehouse(final Long id) {
        return getById(id, warehouseMapper, wmsConvert::toWarehouseDTO, "Warehouse");
    }

    @Override
    public PageResult<ZoneDTO> pageZones(final WmsBaseQueryVO query) {
        final WmsBaseQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsZone> wrapper = new LambdaQueryWrapper<WmsZone>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsZone::getZoneCode, safeQuery.getKeyword())
                        .or()
                        .like(WmsZone::getZoneName, safeQuery.getKeyword()))
                .eq(safeQuery.getWarehouseId() != null, WmsZone::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getZoneId() != null, WmsZone::getId, safeQuery.getZoneId())
                .eq(safeQuery.getStatus() != null, WmsZone::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsZone::getId);
        return toPage(safeQuery, zoneMapper, wrapper, wmsConvert::toZoneDTO);
    }

    @Override
    public ZoneDTO getZone(final Long id) {
        return getById(id, zoneMapper, wmsConvert::toZoneDTO, "Zone");
    }

    @Override
    public PageResult<LocationDTO> pageLocations(final WmsBaseQueryVO query) {
        final WmsBaseQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsLocation> wrapper = new LambdaQueryWrapper<WmsLocation>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsLocation::getLocationCode, safeQuery.getKeyword())
                        .or()
                        .like(WmsLocation::getLocationName, safeQuery.getKeyword()))
                .eq(safeQuery.getWarehouseId() != null, WmsLocation::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getZoneId() != null, WmsLocation::getZoneId, safeQuery.getZoneId())
                .eq(safeQuery.getStatus() != null, WmsLocation::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsLocation::getId);
        return toPage(safeQuery, locationMapper, wrapper, wmsConvert::toLocationDTO);
    }

    @Override
    public LocationDTO getLocation(final Long id) {
        return getById(id, locationMapper, wmsConvert::toLocationDTO, "Location");
    }

    @Override
    public PageResult<LineDTO> pageLines(final WmsBaseQueryVO query) {
        final WmsBaseQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsLine> wrapper = new LambdaQueryWrapper<WmsLine>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsLine::getLineCode, safeQuery.getKeyword())
                        .or()
                        .like(WmsLine::getLineName, safeQuery.getKeyword()))
                .eq(safeQuery.getWarehouseId() != null, WmsLine::getWarehouseId, safeQuery.getWarehouseId())
                .eq(safeQuery.getLineId() != null, WmsLine::getId, safeQuery.getLineId())
                .eq(safeQuery.getDriverAccountId() != null, WmsLine::getDefaultDriverAccountId,
                        safeQuery.getDriverAccountId())
                .eq(safeQuery.getStatus() != null, WmsLine::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsLine::getId);
        return toPage(safeQuery, lineMapper, wrapper, wmsConvert::toLineDTO);
    }

    @Override
    public LineDTO getLine(final Long id) {
        return getById(id, lineMapper, wmsConvert::toLineDTO, "Line");
    }

    @Override
    public PageResult<LineStationDTO> pageLineStations(final WmsBaseQueryVO query) {
        final WmsBaseQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsLineStation> wrapper = new LambdaQueryWrapper<WmsLineStation>()
                .eq(safeQuery.getLineId() != null, WmsLineStation::getLineId, safeQuery.getLineId())
                .eq(safeQuery.getStationId() != null, WmsLineStation::getStationId, safeQuery.getStationId())
                .eq(safeQuery.getStatus() != null, WmsLineStation::getStatus, safeQuery.getStatus())
                .orderByAsc(WmsLineStation::getDeliverySort)
                .orderByDesc(WmsLineStation::getId);
        return toPage(safeQuery, lineStationMapper, wrapper, wmsConvert::toLineStationDTO);
    }

    @Override
    public LineStationDTO getLineStation(final Long id) {
        return getById(id, lineStationMapper, wmsConvert::toLineStationDTO, "LineStation");
    }

    @Override
    public PageResult<DriverDTO> pageDrivers(final WmsBaseQueryVO query) {
        final WmsBaseQueryVO safeQuery = safeQuery(query);
        final LambdaQueryWrapper<WmsDriver> wrapper = new LambdaQueryWrapper<WmsDriver>()
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(WmsDriver::getDriverNo, safeQuery.getKeyword())
                        .or()
                        .like(WmsDriver::getDriverName, safeQuery.getKeyword())
                        .or()
                        .like(WmsDriver::getDriverMobile, safeQuery.getKeyword())
                        .or()
                        .like(WmsDriver::getVehicleNo, safeQuery.getKeyword()))
                .eq(safeQuery.getDriverAccountId() != null, WmsDriver::getAccountId,
                        safeQuery.getDriverAccountId())
                .eq(safeQuery.getStatus() != null, WmsDriver::getStatus, safeQuery.getStatus())
                .orderByDesc(WmsDriver::getId);
        return toPage(safeQuery, driverMapper, wrapper, wmsConvert::toDriverDTO);
    }

    @Override
    public DriverDTO getDriver(final Long id) {
        return getById(id, driverMapper, wmsConvert::toDriverDTO, "Driver");
    }

    private WmsBaseQueryVO safeQuery(final WmsBaseQueryVO query) {
        return query == null ? new WmsBaseQueryVO() : query;
    }
}
