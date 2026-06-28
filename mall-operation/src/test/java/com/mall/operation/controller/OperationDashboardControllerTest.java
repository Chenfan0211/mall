package com.mall.operation.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.operation.dto.OperationDashboardSummaryDTO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.operation.service.OperationDashboardService;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OperationDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OperationDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OperationDashboardService operationDashboardService;

    @Test
    void getSummary_success() throws Exception {
        final OperationDashboardSummaryDTO summary = new OperationDashboardSummaryDTO();
        summary.setPendingTodoCount(2L);
        summary.setHighPriorityExceptionCount(1L);
        when(operationDashboardService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/operation/dashboard/summary")
                        .with(user("tester").authorities(() -> "operation:dashboard:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.pendingTodoCount").value(2))
                .andExpect(jsonPath("$.data.highPriorityExceptionCount").value(1));
    }

    @Test
    void getSummary_adminEndpoint_hasPreAuthorize() throws Exception {
        final Method method = OperationDashboardController.class.getDeclaredMethod("getSummary");

        assertNotNull(method.getAnnotation(PreAuthorize.class));
    }
}
