package com.mall.wms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.wms.dto.InboundOrderDTO;
import com.mall.api.wms.vo.WmsInboundQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.wms.convert.WmsConvert;
import com.mall.wms.entity.WmsInboundOrder;
import com.mall.wms.mapper.WmsInboundItemMapper;
import com.mall.wms.mapper.WmsInboundOrderMapper;
import com.mall.wms.mapper.WmsPutawayTaskMapper;
import com.mall.wms.mapper.WmsQualityFileMapper;
import com.mall.wms.mapper.WmsReceiveRecordMapper;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WmsInboundServiceImplTest {

    @BeforeAll
    static void initMybatisPlusTableInfo() {
        final MapperBuilderAssistant assistant = new MapperBuilderAssistant(new Configuration(), "");
        assistant.setCurrentNamespace(WmsInboundOrderMapper.class.getName());
        TableInfoHelper.initTableInfo(assistant, WmsInboundOrder.class);
    }

    @Mock
    private WmsInboundOrderMapper inboundOrderMapper;

    @Mock
    private WmsInboundItemMapper inboundItemMapper;

    @Mock
    private WmsReceiveRecordMapper receiveRecordMapper;

    @Mock
    private WmsQualityFileMapper qualityFileMapper;

    @Mock
    private WmsPutawayTaskMapper putawayTaskMapper;

    @Mock
    private WmsConvert wmsConvert;

    @InjectMocks
    private WmsInboundServiceImpl wmsInboundService;

    @Test
    void pageInboundOrders_success() {
        final WmsInboundOrder inboundOrder = new WmsInboundOrder();
        inboundOrder.setId(781001L);
        inboundOrder.setInboundNo("INB_TEST_WAIT_ARRIVAL");
        final InboundOrderDTO inboundOrderDTO = new InboundOrderDTO();
        inboundOrderDTO.setId(781001L);
        inboundOrderDTO.setInboundNo("INB_TEST_WAIT_ARRIVAL");
        final Page<WmsInboundOrder> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(inboundOrder));
        when(inboundOrderMapper.selectPage(any(), any())).thenReturn(page);
        when(wmsConvert.toInboundOrderDTO(inboundOrder)).thenReturn(inboundOrderDTO);

        final PageResult<InboundOrderDTO> result = wmsInboundService.pageInboundOrders(null);

        assertEquals(1L, result.getTotal());
        assertEquals("INB_TEST_WAIT_ARRIVAL", result.getList().get(0).getInboundNo());
    }

    @Test
    void pageInboundOrders_createDate_filterByCreateTimeRange() {
        final Page<WmsInboundOrder> page = Page.of(1L, 20L);
        page.setTotal(0L);
        page.setRecords(List.of());
        when(inboundOrderMapper.selectPage(any(), any())).thenReturn(page);
        final WmsInboundQueryVO query = new WmsInboundQueryVO();
        query.setCreateDate(LocalDate.of(2026, 7, 2));
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final ArgumentCaptor<Wrapper<WmsInboundOrder>> wrapperCaptor = ArgumentCaptor.forClass((Class) Wrapper.class);

        wmsInboundService.pageInboundOrders(query);

        verify(inboundOrderMapper).selectPage(any(), wrapperCaptor.capture());
        final String sqlSegment = wrapperCaptor.getValue().getSqlSegment();
        assertTrue(sqlSegment.contains("create_time >= #{ew.paramNameValuePairs."));
        assertTrue(sqlSegment.contains("create_time < #{ew.paramNameValuePairs."));
    }

    @Test
    void getInboundOrder_notFound_throwNotFound() {
        when(inboundOrderMapper.selectById(781999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> wmsInboundService.getInboundOrder(781999L));
    }
}
