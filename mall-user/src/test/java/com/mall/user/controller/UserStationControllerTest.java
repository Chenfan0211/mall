package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.user.dto.UserStationDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserStationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserStationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserStationService userStationService;

    @Test
    void pageStations_success() throws Exception {
        final UserStationDTO stationDTO = new UserStationDTO();
        stationDTO.setId(720001L);
        stationDTO.setStationName("海珠测试自提点");
        when(userStationService.pageStations(any())).thenReturn(PageResult.of(1L, List.of(stationDTO)));

        mockMvc.perform(get("/api/user/stations")
                        .param("pageNum", "1")
                        .param("pageSize", "20")
                        .with(user("tester").authorities(() -> "user:station:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].stationName").value("海珠测试自提点"));
    }

    @Test
    void getStation_success() throws Exception {
        final UserStationDTO stationDTO = new UserStationDTO();
        stationDTO.setId(720001L);
        stationDTO.setStationName("海珠测试自提点");
        when(userStationService.getStation(eq(720001L))).thenReturn(stationDTO);

        mockMvc.perform(get("/api/user/stations/{id}", 720001L)
                        .with(user("tester").authorities(() -> "user:station:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.stationName").value("海珠测试自提点"));
    }
}
