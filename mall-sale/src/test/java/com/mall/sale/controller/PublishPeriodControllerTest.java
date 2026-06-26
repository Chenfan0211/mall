package com.mall.sale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.sale.dto.PublishPeriodDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.sale.service.PublishPeriodService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PublishPeriodController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PublishPeriodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublishPeriodService publishPeriodService;

    @Test
    void pagePeriods_success() throws Exception {
        final PublishPeriodDTO periodDTO = new PublishPeriodDTO();
        periodDTO.setId(755002L);
        periodDTO.setPeriodName("测试已发布团期");
        when(publishPeriodService.pagePeriods(any())).thenReturn(PageResult.of(1L, List.of(periodDTO)));

        mockMvc.perform(get("/api/sale/periods")
                        .param("status", "20")
                        .with(user("tester").authorities(() -> "sale:period:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].periodName").value("测试已发布团期"));
    }

    @Test
    void pagePeriods_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/sale/periods")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "sale:period:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
