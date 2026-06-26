package com.mall.sale.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.sale.dto.PeriodSnapshotDTO;
import com.mall.api.sale.dto.PublishPeriodDTO;
import com.mall.api.sale.vo.PublishPeriodQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.sale.convert.SaleConvert;
import com.mall.sale.entity.SalePeriodSnapshot;
import com.mall.sale.entity.SalePublishPeriod;
import com.mall.sale.mapper.SalePeriodSnapshotMapper;
import com.mall.sale.mapper.SalePublishPeriodMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublishPeriodServiceImplTest {

    @Mock
    private SalePublishPeriodMapper publishPeriodMapper;

    @Mock
    private SalePeriodSnapshotMapper periodSnapshotMapper;

    @Mock
    private SaleConvert saleConvert;

    @InjectMocks
    private PublishPeriodServiceImpl publishPeriodService;

    @Test
    void pagePeriods_success() {
        final PublishPeriodQueryVO query = new PublishPeriodQueryVO();
        query.setStatus(20L);
        final SalePublishPeriod period = new SalePublishPeriod();
        period.setId(755002L);
        period.setPeriodName("测试已发布团期");
        final PublishPeriodDTO periodDTO = new PublishPeriodDTO();
        periodDTO.setId(755002L);
        periodDTO.setPeriodName("测试已发布团期");
        final Page<SalePublishPeriod> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(period));
        when(publishPeriodMapper.selectPage(any(), any())).thenReturn(page);
        when(saleConvert.toPublishPeriodDTO(period)).thenReturn(periodDTO);

        final PageResult<PublishPeriodDTO> result = publishPeriodService.pagePeriods(query);

        assertEquals(1L, result.getTotal());
        assertEquals("测试已发布团期", result.getList().get(0).getPeriodName());
    }

    @Test
    void getPeriod_notFound_throwNotFound() {
        when(publishPeriodMapper.selectById(755999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> publishPeriodService.getPeriod(755999L));
    }

    @Test
    void listSnapshots_success() {
        final SalePeriodSnapshot snapshot = new SalePeriodSnapshot();
        snapshot.setId(756201L);
        snapshot.setSnapshotNode(1L);
        final PeriodSnapshotDTO snapshotDTO = new PeriodSnapshotDTO();
        snapshotDTO.setId(756201L);
        snapshotDTO.setSnapshotNode(1L);
        when(periodSnapshotMapper.selectList(any())).thenReturn(List.of(snapshot));
        when(saleConvert.toPeriodSnapshotDTO(snapshot)).thenReturn(snapshotDTO);

        final List<PeriodSnapshotDTO> result = publishPeriodService.listSnapshots(755002L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSnapshotNode());
    }
}
