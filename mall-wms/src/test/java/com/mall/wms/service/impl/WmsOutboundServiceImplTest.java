package com.mall.wms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.wms.dto.WaveBatchDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsWaveBatch;
import com.mall.wms.mapper.WmsDeliveryOrderMapper;
import com.mall.wms.mapper.WmsDeliveryStationMapper;
import com.mall.wms.mapper.WmsDriverSignRecordMapper;
import com.mall.wms.mapper.WmsLoadingItemMapper;
import com.mall.wms.mapper.WmsLoadingOrderMapper;
import com.mall.wms.mapper.WmsOperationLogMapper;
import com.mall.wms.mapper.WmsOutboundOrderMapper;
import com.mall.wms.mapper.WmsPickItemMapper;
import com.mall.wms.mapper.WmsPickTaskMapper;
import com.mall.wms.mapper.WmsReturnHandoverMapper;
import com.mall.wms.mapper.WmsWaveBatchMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WmsOutboundServiceImplTest {

    @Mock
    private WmsWaveBatchMapper waveBatchMapper;

    @Mock
    private WmsPickTaskMapper pickTaskMapper;

    @Mock
    private WmsPickItemMapper pickItemMapper;

    @Mock
    private WmsLoadingOrderMapper loadingOrderMapper;

    @Mock
    private WmsLoadingItemMapper loadingItemMapper;

    @Mock
    private WmsOutboundOrderMapper outboundOrderMapper;

    @Mock
    private WmsDeliveryOrderMapper deliveryOrderMapper;

    @Mock
    private WmsDeliveryStationMapper deliveryStationMapper;

    @Mock
    private WmsDriverSignRecordMapper driverSignRecordMapper;

    @Mock
    private WmsReturnHandoverMapper returnHandoverMapper;

    @Mock
    private WmsOperationLogMapper operationLogMapper;

    @Mock
    private WmsConvert wmsConvert;

    @InjectMocks
    private WmsOutboundServiceImpl wmsOutboundService;

    @Test
    void pageWaveBatches_success() {
        final WmsWaveBatch waveBatch = new WmsWaveBatch();
        waveBatch.setId(784001L);
        waveBatch.setWaveNo("WAVE_TEST_WAIT_LOCK");
        final WaveBatchDTO waveBatchDTO = new WaveBatchDTO();
        waveBatchDTO.setId(784001L);
        waveBatchDTO.setWaveNo("WAVE_TEST_WAIT_LOCK");
        final Page<WmsWaveBatch> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(waveBatch));
        when(waveBatchMapper.selectPage(any(), any())).thenReturn(page);
        when(wmsConvert.toWaveBatchDTO(waveBatch)).thenReturn(waveBatchDTO);

        final PageResult<WaveBatchDTO> result = wmsOutboundService.pageWaveBatches(null);

        assertEquals(1L, result.getTotal());
        assertEquals("WAVE_TEST_WAIT_LOCK", result.getList().get(0).getWaveNo());
    }

    @Test
    void getWaveBatch_notFound_throwNotFound() {
        when(waveBatchMapper.selectById(784999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> wmsOutboundService.getWaveBatch(784999L));
    }
}
