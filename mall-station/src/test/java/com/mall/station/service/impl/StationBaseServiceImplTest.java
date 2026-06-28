package com.mall.station.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.station.dto.StationDTO;
import com.mall.api.station.dto.StationLeaveDTO;
import com.mall.api.station.vo.StationLeaveApplyVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.station.convert.StationConvert;
import com.mall.station.entity.UsrStation;
import com.mall.station.entity.UsrStationLeave;
import com.mall.station.mapper.UsrLeaderMapper;
import com.mall.station.mapper.UsrStationLeaderMapper;
import com.mall.station.mapper.UsrStationLeaveMapper;
import com.mall.station.mapper.UsrStationMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StationBaseServiceImplTest {

    @Mock
    private UsrStationMapper stationMapper;

    @Mock
    private UsrLeaderMapper leaderMapper;

    @Mock
    private UsrStationLeaderMapper stationLeaderMapper;

    @Mock
    private UsrStationLeaveMapper stationLeaveMapper;

    @Mock
    private StationConvert stationConvert;

    @InjectMocks
    private StationBaseServiceImpl stationBaseService;

    @Test
    void pageStations_success() {
        final UsrStation station = new UsrStation();
        station.setId(720001L);
        station.setStationName("海珠测试自提点");
        final StationDTO stationDTO = new StationDTO();
        stationDTO.setId(720001L);
        stationDTO.setStationName("海珠测试自提点");
        final Page<UsrStation> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(station));
        when(stationMapper.selectPage(any(), any())).thenReturn(page);
        when(stationConvert.toStationDTO(station)).thenReturn(stationDTO);

        final PageResult<StationDTO> result = stationBaseService.pageStations(null);

        assertEquals(1L, result.getTotal());
        assertEquals("海珠测试自提点", result.getList().get(0).getStationName());
    }

    @Test
    void getStation_notFound_throwNotFound() {
        when(stationMapper.selectById(720999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> stationBaseService.getStation(720999L));
    }

    @Test
    void applyLeave_success() {
        final StationLeaveApplyVO request = new StationLeaveApplyVO();
        request.setStationId(720001L);
        request.setStartTime(LocalDateTime.now().plusDays(1L));
        request.setEndTime(LocalDateTime.now().plusDays(2L));
        request.setReason("门店装修");
        final UsrStation station = new UsrStation();
        station.setId(720001L);
        station.setCityId(440100L);
        final StationLeaveDTO leaveDTO = new StationLeaveDTO();
        leaveDTO.setStationId(720001L);
        leaveDTO.setAuditStatus(10L);
        when(stationMapper.selectById(720001L)).thenReturn(station);
        when(stationLeaveMapper.selectCount(any())).thenReturn(0L);
        when(stationLeaveMapper.insert(any(UsrStationLeave.class))).thenReturn(1);
        when(stationConvert.toStationLeaveDTO(any())).thenReturn(leaveDTO);

        final StationLeaveDTO result = stationBaseService.applyLeave(request);

        assertEquals(10L, result.getAuditStatus());
        verify(stationLeaveMapper).insert(any(UsrStationLeave.class));
    }

    @Test
    void cancelLeave_waitAudit_success() {
        final UsrStationLeave leave = new UsrStationLeave();
        leave.setId(721101L);
        leave.setAuditStatus(10L);
        final StationLeaveDTO leaveDTO = new StationLeaveDTO();
        leaveDTO.setId(721101L);
        leaveDTO.setAuditStatus(40L);
        when(stationLeaveMapper.selectById(721101L)).thenReturn(leave);
        when(stationLeaveMapper.updateById(leave)).thenReturn(1);
        when(stationConvert.toStationLeaveDTO(leave)).thenReturn(leaveDTO);

        final StationLeaveDTO result = stationBaseService.cancelLeave(721101L);

        assertEquals(40L, result.getAuditStatus());
    }
}
