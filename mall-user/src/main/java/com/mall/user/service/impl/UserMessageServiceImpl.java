package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.user.dto.UserMessageDTO;
import com.mall.api.user.vo.UserMessageQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.common.result.CommonErrorCode;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.MsgRecord;
import com.mall.user.mapper.MsgRecordMapper;
import com.mall.user.service.UserMessageService;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserMessageServiceImpl extends UserReadSupport implements UserMessageService {

    private static final Long MSG_RECEIVER_USER = 1L;
    private static final Long MSG_READ = 1L;

    private final MsgRecordMapper msgRecordMapper;
    private final UserConvert userConvert;

    public UserMessageServiceImpl(final MsgRecordMapper msgRecordMapper, final UserConvert userConvert) {
        this.msgRecordMapper = msgRecordMapper;
        this.userConvert = userConvert;
    }

    @Override
    public PageResult<UserMessageDTO> pageMessages(final UserMessageQueryVO query) {
        final UserMessageQueryVO safeQuery = validateQuery(query);
        final LambdaQueryWrapper<MsgRecord> wrapper = new LambdaQueryWrapper<MsgRecord>()
                .eq(MsgRecord::getReceiverType, MSG_RECEIVER_USER)
                .eq(MsgRecord::getReceiverId, safeQuery.getUserId())
                .and(StringUtils.hasText(safeQuery.getKeyword()), nested -> nested
                        .like(MsgRecord::getMsgNo, safeQuery.getKeyword())
                        .or()
                        .like(MsgRecord::getTitle, safeQuery.getKeyword())
                        .or()
                        .like(MsgRecord::getContent, safeQuery.getKeyword()))
                .eq(safeQuery.getChannelType() != null, MsgRecord::getChannelType, safeQuery.getChannelType())
                .eq(safeQuery.getReadStatus() != null, MsgRecord::getReadStatus, safeQuery.getReadStatus())
                .eq(safeQuery.getSendStatus() != null, MsgRecord::getSendStatus, safeQuery.getSendStatus())
                .eq(StringUtils.hasText(safeQuery.getBizType()), MsgRecord::getBizType, safeQuery.getBizType())
                .eq(StringUtils.hasText(safeQuery.getBizNo()), MsgRecord::getBizNo, safeQuery.getBizNo())
                .orderByDesc(MsgRecord::getId);
        return toPage(safeQuery, msgRecordMapper, wrapper, userConvert::toMessageDTO);
    }

    @Override
    public UserMessageDTO getMessage(final Long id, final Long userId) {
        return userConvert.toMessageDTO(loadUserMessage(id, userId));
    }

    @Override
    @Transactional
    public UserMessageDTO markMessageRead(final Long id, final Long userId) {
        final MsgRecord message = loadUserMessage(id, userId);
        if (Objects.equals(message.getReadStatus(), MSG_READ)) {
            return userConvert.toMessageDTO(message);
        }
        final LocalDateTime now = LocalDateTime.now();
        message.setReadStatus(MSG_READ);
        prepareUpdate(message, now);
        msgRecordMapper.updateById(message);
        return userConvert.toMessageDTO(message);
    }

    private UserMessageQueryVO validateQuery(final UserMessageQueryVO query) {
        if (query == null || query.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        return query;
    }

    private MsgRecord loadUserMessage(final Long id, final Long userId) {
        if (id == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "消息ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        final MsgRecord message = msgRecordMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "消息不存在");
        }
        if (!Objects.equals(message.getReceiverType(), MSG_RECEIVER_USER) || !userId.equals(message.getReceiverId())) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN, "不能查看其他用户消息");
        }
        return message;
    }
}
