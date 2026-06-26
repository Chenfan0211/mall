package com.mall.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mall.api.user.dto.UserMessageDTO;
import com.mall.common.page.PageResult;
import com.mall.common.web.GlobalExceptionHandler;
import com.mall.user.service.UserMessageService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMessageService userMessageService;

    @Test
    void pageMessages_success() throws Exception {
        final UserMessageDTO messageDTO = new UserMessageDTO();
        messageDTO.setId(792101L);
        messageDTO.setTitle("订单待支付");
        when(userMessageService.pageMessages(any())).thenReturn(PageResult.of(1L, List.of(messageDTO)));

        mockMvc.perform(get("/api/user/messages")
                        .param("userId", "740001")
                        .with(user("tester").authorities(() -> "user:message:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.list[0].title").value("订单待支付"));
    }

    @Test
    void markMessageRead_success() throws Exception {
        final UserMessageDTO messageDTO = new UserMessageDTO();
        messageDTO.setId(792101L);
        messageDTO.setReadStatus(1L);
        when(userMessageService.markMessageRead(eq(792101L), eq(740001L))).thenReturn(messageDTO);

        mockMvc.perform(post("/api/user/messages/792101/read")
                        .param("userId", "740001")
                        .with(user("tester").authorities(() -> "user:message:read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.readStatus").value("1"));
    }
}
