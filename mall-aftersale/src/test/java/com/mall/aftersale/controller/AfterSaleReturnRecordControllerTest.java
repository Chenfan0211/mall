package com.mall.aftersale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.aftersale.service.AfterSaleReturnRecordService;
import com.mall.api.aftersale.dto.AfterSaleReturnRecordDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AfterSaleReturnRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AfterSaleReturnRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AfterSaleReturnRecordService returnRecordService;

    @Test
    void pageReturnRecords_success() throws Exception {
        final AfterSaleReturnRecordDTO recordDTO = new AfterSaleReturnRecordDTO();
        recordDTO.setId(767301L);
        recordDTO.setReturnNo("AFS_RET_TEST_WAIT");
        when(returnRecordService.pageReturnRecords(any())).thenReturn(PageResult.of(1L, List.of(recordDTO)));

        mockMvc.perform(get("/api/aftersale/return-records")
                        .param("returnStatus", "10")
                        .with(user("tester").authorities(() -> "aftersale:return-record:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].returnNo").value("AFS_RET_TEST_WAIT"));
    }

    @Test
    void pageReturnRecords_invalidPageSize_returnParamError() throws Exception {
        mockMvc.perform(get("/api/aftersale/return-records")
                        .param("pageSize", "201")
                        .with(user("tester").authorities(() -> "aftersale:return-record:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("pageSize:每页数量不能超过200"));
    }
}
