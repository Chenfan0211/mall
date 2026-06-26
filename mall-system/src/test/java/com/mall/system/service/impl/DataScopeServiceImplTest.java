package com.mall.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.mall.common.security.DataScopeContext;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysDataScope;
import com.mall.system.mapper.SysDataScopeMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataScopeServiceImplTest {

    @Mock
    private SysDataScopeMapper dataScopeMapper;

    @Mock
    private SystemConvert systemConvert;

    @InjectMocks
    private DataScopeServiceImpl dataScopeService;

    @Test
    void buildContextByAccountId_multipleScopes_success() {
        when(dataScopeMapper.selectByAccountId(700001L)).thenReturn(List.of(
                scope(1L, null),
                scope(2L, 440100L),
                scope(3L, 710001L),
                scope(4L, 720001L),
                scope(5L, 50001L),
                scope(6L, 760001L),
                scope(7L, 730001L)));

        final DataScopeContext.DataScope context = dataScopeService.buildContextByAccountId(700001L);

        assertEquals(700001L, context.getAccountId());
        assertTrue(context.isAllPlatform());
        assertTrue(context.getCityIds().contains(440100L));
        assertTrue(context.getSupplierIds().contains(710001L));
        assertTrue(context.getStationIds().contains(720001L));
        assertTrue(context.getWarehouseIds().contains(50001L));
        assertTrue(context.getDriverIds().contains(760001L));
        assertTrue(context.getLeaderIds().contains(730001L));
    }

    private SysDataScope scope(final Long scopeType, final Long scopeObjectId) {
        final SysDataScope scope = new SysDataScope();
        scope.setScopeType(scopeType);
        scope.setScopeObjectId(scopeObjectId);
        return scope;
    }
}
