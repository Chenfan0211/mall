package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.user.dto.UserStationDTO;
import com.mall.api.user.vo.UserStationQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.UsrStation;
import com.mall.user.mapper.UsrStationMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStationServiceImplTest {

    @Mock
    private UsrStationMapper stationMapper;

    @Mock
    private UserConvert userConvert;

    @InjectMocks
    private UserStationServiceImpl userStationService;

    @Test
    void pageStations_success() {
        final UserStationQueryVO query = new UserStationQueryVO();
        query.setKeyword("海珠");
        final UsrStation station = new UsrStation();
        station.setId(720001L);
        station.setStationName("海珠测试自提点");
        final UserStationDTO stationDTO = new UserStationDTO();
        stationDTO.setId(720001L);
        stationDTO.setStationName("海珠测试自提点");
        final Page<UsrStation> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(station));
        when(stationMapper.selectPage(any(), any())).thenReturn(page);
        when(userConvert.toStationDTO(station)).thenReturn(stationDTO);

        final PageResult<UserStationDTO> result = userStationService.pageStations(query);

        assertEquals(1L, result.getTotal());
        assertEquals("海珠测试自提点", result.getList().get(0).getStationName());
    }

    @Test
    void getStation_nullId_throwParamError() {
        assertThrows(BusinessException.class, () -> userStationService.getStation(null));
    }
}
