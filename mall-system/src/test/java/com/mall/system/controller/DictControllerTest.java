package com.mall.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.system.dto.DictItemDTO;
import com.mall.api.system.vo.DictItemQueryVO;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.system.service.DictService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DictController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DictControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DictService dictService;

    @Test
    void listItems_success() throws Exception {
        final DictItemDTO dictItemDTO = new DictItemDTO();
        dictItemDTO.setId(801001L);
        dictItemDTO.setItemCode("ORDER_WAIT_PAY");
        dictItemDTO.setItemName("待支付");
        when(dictService.listItems(any(DictItemQueryVO.class))).thenReturn(List.of(dictItemDTO));

        mockMvc.perform(get("/api/system/dicts/items")
                        .param("dictCode", "order_status")
                        .with(user("tester").authorities(() -> "system:dict:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data[0].itemName").value("待支付"));
    }

    @Test
    void listItems_missingDictCode_returnParamError() throws Exception {
        mockMvc.perform(get("/api/system/dicts/items")
                        .with(user("tester").authorities(() -> "system:dict:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("dictCode:字典编码不能为空"));
    }
}
