package com.mall.station.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.station.dto.StationDTO;
import com.mall.api.station.dto.StationLeaveDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.station.service.StationBaseService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StationBaseController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StationBaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationBaseService stationBaseService;

    @Test
    void pageStations_success() throws Exception {
        final StationDTO stationDTO = new StationDTO();
        stationDTO.setId(720001L);
        stationDTO.setStationName("海珠测试自提点");
        when(stationBaseService.pageStations(any())).thenReturn(PageResult.of(1L, List.of(stationDTO)));

        mockMvc.perform(get("/api/station/base/stations")
                        .param("status", "1")
                        .with(user("tester").authorities(() -> "station:base:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].stationName").value("海珠测试自提点"));
    }

    @Test
    void getStation_success() throws Exception {
        final StationDTO stationDTO = new StationDTO();
        stationDTO.setId(720001L);
        stationDTO.setStationNo("ST_TEST_OPEN");
        when(stationBaseService.getStation(720001L)).thenReturn(stationDTO);

        mockMvc.perform(get("/api/station/base/stations/720001")
                        .with(user("tester").authorities(() -> "station:base:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.stationNo").value("ST_TEST_OPEN"));
    }

    @Test
    void pageLeaves_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/station/base/leaves")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "station:leave:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void applyLeave_success() throws Exception {
        final StationLeaveDTO leaveDTO = new StationLeaveDTO();
        leaveDTO.setId(721101L);
        leaveDTO.setAuditStatus(10L);
        when(stationBaseService.applyLeave(any())).thenReturn(leaveDTO);

        mockMvc.perform(post("/api/station/base/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stationId\":720001,\"startTime\":\"2026-06-26T09:00:00\","
                                + "\"endTime\":\"2026-06-27T18:00:00\",\"reason\":\"门店装修\"}")
                        .with(user("tester").authorities(() -> "station:leave:apply")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.auditStatus").value("10"));
    }

    @Test
    void cancelLeave_success() throws Exception {
        final StationLeaveDTO leaveDTO = new StationLeaveDTO();
        leaveDTO.setId(721101L);
        leaveDTO.setAuditStatus(40L);
        when(stationBaseService.cancelLeave(eq(721101L))).thenReturn(leaveDTO);

        mockMvc.perform(post("/api/station/base/leaves/721101/cancel")
                        .with(user("tester").authorities(() -> "station:leave:cancel")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.auditStatus").value("40"));
    }

    @Test
    void applyLeave_missingReason_returnParamError() throws Exception {
        mockMvc.perform(post("/api/station/base/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stationId\":720001,\"startTime\":\"2026-06-26T09:00:00\","
                                + "\"endTime\":\"2026-06-27T18:00:00\"}")
                        .with(user("tester").authorities(() -> "station:leave:apply")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
