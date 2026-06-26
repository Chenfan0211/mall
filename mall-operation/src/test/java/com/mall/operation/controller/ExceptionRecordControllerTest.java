package com.mall.operation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.operation.service.ExceptionRecordService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExceptionRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ExceptionRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExceptionRecordService exceptionRecordService;

    @Test
    void pageExceptionRecords_success() throws Exception {
        final ExceptionRecordDTO recordDTO = new ExceptionRecordDTO();
        recordDTO.setId(791101L);
        recordDTO.setTitle("拣货差异待处理");
        when(exceptionRecordService.pageExceptionRecords(any())).thenReturn(PageResult.of(1L, List.of(recordDTO)));

        mockMvc.perform(get("/api/operation/exceptions")
                        .param("status", "10")
                        .with(user("tester").authorities(() -> "operation:exception:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("拣货差异待处理"));
    }
}
