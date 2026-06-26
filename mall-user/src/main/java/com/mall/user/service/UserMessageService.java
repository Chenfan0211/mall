package com.mall.user.service;

import com.mall.api.user.dto.UserMessageDTO;
import com.mall.api.user.vo.UserMessageQueryVO;
import com.mall.common.page.PageResult;

public interface UserMessageService {

    PageResult<UserMessageDTO> pageMessages(UserMessageQueryVO query);

    UserMessageDTO getMessage(Long id, Long userId);

    UserMessageDTO markMessageRead(Long id, Long userId);
}
