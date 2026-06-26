package com.mall.wms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.wms.dto.WarehouseDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsWarehouse;
import com.mall.wms.mapper.WmsDriverMapper;
import com.mall.wms.mapper.WmsLineMapper;
import com.mall.wms.mapper.WmsLineStationMapper;
import com.mall.wms.mapper.WmsLocationMapper;
import com.mall.wms.mapper.WmsWarehouseMapper;
import com.mall.wms.mapper.WmsZoneMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WmsBaseServiceImplTest {

    @Mock
    private WmsWarehouseMapper warehouseMapper;

    @Mock
    private WmsZoneMapper zoneMapper;

    @Mock
    private WmsLocationMapper locationMapper;

    @Mock
    private WmsLineMapper lineMapper;

    @Mock
    private WmsLineStationMapper lineStationMapper;

    @Mock
    private WmsDriverMapper driverMapper;

    @Mock
    private WmsConvert wmsConvert;

    @InjectMocks
    private WmsBaseServiceImpl wmsBaseService;

    @Test
    void pageWarehouses_success() {
        final WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setId(50001L);
        warehouse.setWarehouseCode("WH_GZ_CENTER");
        final WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(50001L);
        warehouseDTO.setWarehouseCode("WH_GZ_CENTER");
        final Page<WmsWarehouse> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(warehouse));
        when(warehouseMapper.selectPage(any(), any())).thenReturn(page);
        when(wmsConvert.toWarehouseDTO(warehouse)).thenReturn(warehouseDTO);

        final PageResult<WarehouseDTO> result = wmsBaseService.pageWarehouses(null);

        assertEquals(1L, result.getTotal());
        assertEquals("WH_GZ_CENTER", result.getList().get(0).getWarehouseCode());
    }

    @Test
    void getWarehouse_notFound_throwNotFound() {
        when(warehouseMapper.selectById(50099L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> wmsBaseService.getWarehouse(50099L));
    }
}
