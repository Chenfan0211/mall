package com.mall.system.service.impl;

import com.mall.api.system.dto.DataScopeDTO;
import com.mall.common.security.DataScopeContext;
import com.mall.common.security.DataScopeType;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysDataScope;
import com.mall.system.mapper.SysDataScopeMapper;
import com.mall.system.service.DataScopeService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DataScopeServiceImpl implements DataScopeService {

    private final SysDataScopeMapper dataScopeMapper;
    private final SystemConvert systemConvert;

    public DataScopeServiceImpl(final SysDataScopeMapper dataScopeMapper, final SystemConvert systemConvert) {
        this.dataScopeMapper = dataScopeMapper;
        this.systemConvert = systemConvert;
    }

    @Override
    public List<DataScopeDTO> listByAccountId(final Long accountId) {
        return systemConvert.toDataScopeDTOList(dataScopeMapper.selectByAccountId(accountId));
    }

    @Override
    public DataScopeContext.DataScope buildContextByAccountId(final Long accountId) {
        final List<SysDataScope> scopes = dataScopeMapper.selectByAccountId(accountId);
        final List<SysDataScope> safeScopes = scopes == null ? Collections.emptyList() : scopes;
        final DataScopeContext.DataScope context = new DataScopeContext.DataScope();
        context.setAccountId(accountId);
        final Set<Long> cityIds = new HashSet<>();
        final Set<Long> supplierIds = new HashSet<>();
        final Set<Long> stationIds = new HashSet<>();
        final Set<Long> warehouseIds = new HashSet<>();
        final Set<Long> driverIds = new HashSet<>();
        final Set<Long> leaderIds = new HashSet<>();
        for (final SysDataScope scope : safeScopes) {
            if (scope == null) {
                continue;
            }
            appendScope(context, cityIds, supplierIds, stationIds, warehouseIds, driverIds, leaderIds, scope);
        }
        context.setCityIds(cityIds);
        context.setSupplierIds(supplierIds);
        context.setStationIds(stationIds);
        context.setWarehouseIds(warehouseIds);
        context.setDriverIds(driverIds);
        context.setLeaderIds(leaderIds);
        return context;
    }

    private void appendScope(
            final DataScopeContext.DataScope context,
            final Set<Long> cityIds,
            final Set<Long> supplierIds,
            final Set<Long> stationIds,
            final Set<Long> warehouseIds,
            final Set<Long> driverIds,
            final Set<Long> leaderIds,
            final SysDataScope scope) {
        if (DataScopeType.ALL_PLATFORM.getCode().equals(scope.getScopeType())) {
            context.setAllPlatform(true);
            return;
        }
        if (scope.getScopeObjectId() == null) {
            return;
        }
        if (DataScopeType.CITY.getCode().equals(scope.getScopeType())) {
            cityIds.add(scope.getScopeObjectId());
        } else if (DataScopeType.SUPPLIER.getCode().equals(scope.getScopeType())) {
            supplierIds.add(scope.getScopeObjectId());
        } else if (DataScopeType.STATION.getCode().equals(scope.getScopeType())) {
            stationIds.add(scope.getScopeObjectId());
        } else if (DataScopeType.WAREHOUSE.getCode().equals(scope.getScopeType())) {
            warehouseIds.add(scope.getScopeObjectId());
        } else if (DataScopeType.DRIVER.getCode().equals(scope.getScopeType())) {
            driverIds.add(scope.getScopeObjectId());
        } else if (DataScopeType.LEADER.getCode().equals(scope.getScopeType())) {
            leaderIds.add(scope.getScopeObjectId());
        }
    }
}
