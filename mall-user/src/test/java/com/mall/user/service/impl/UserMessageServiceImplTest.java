package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mall.api.user.dto.UserMessageDTO;
import com.mall.common.exception.BusinessException;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.MsgRecord;
import com.mall.user.mapper.MsgRecordMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMessageServiceImplTest {

    @Mock
    private MsgRecordMapper msgRecordMapper;

    @Mock
    private UserConvert userConvert;

    @InjectMocks
    private UserMessageServiceImpl userMessageService;

    @Test
    void markMessageRead_unread_success() {
        final MsgRecord message = new MsgRecord();
        message.setId(792101L);
        message.setReceiverType(1L);
        message.setReceiverId(740001L);
        message.setReadStatus(0L);
        final UserMessageDTO dto = new UserMessageDTO();
        dto.setId(792101L);
        dto.setReadStatus(1L);
        when(msgRecordMapper.selectById(792101L)).thenReturn(message);
        when(msgRecordMapper.updateById(message)).thenReturn(1);
        when(userConvert.toMessageDTO(message)).thenReturn(dto);

        final UserMessageDTO result = userMessageService.markMessageRead(792101L, 740001L);

        assertEquals(1L, message.getReadStatus());
        assertEquals(1L, result.getReadStatus());
        verify(msgRecordMapper).updateById(message);
    }

    @Test
    void getMessage_otherUser_throwForbidden() {
        final MsgRecord message = new MsgRecord();
        message.setId(792101L);
        message.setReceiverType(1L);
        message.setReceiverId(740001L);
        when(msgRecordMapper.selectById(792101L)).thenReturn(message);

        assertThrows(BusinessException.class, () -> userMessageService.getMessage(792101L, 740002L));
    }
}
