package com.mall.user.controller;

import com.mall.api.user.dto.UserMessageDTO;
import com.mall.api.user.vo.UserMessageQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.user.service.UserMessageService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user/messages")
public class UserMessageController {

    private final UserMessageService userMessageService;

    public UserMessageController(final UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    @GetMapping
    public Result<PageResult<UserMessageDTO>> pageMessages(@Valid final UserMessageQueryVO query) {
        return Result.success(userMessageService.pageMessages(query));
    }

    @GetMapping("/{id}")
    public Result<UserMessageDTO> getMessage(@PathVariable final Long id, @RequestParam final Long userId) {
        return Result.success(userMessageService.getMessage(id, userId));
    }

    @PostMapping("/{id}/read")
    public Result<UserMessageDTO> markMessageRead(@PathVariable final Long id, @RequestParam final Long userId) {
        return Result.success(userMessageService.markMessageRead(id, userId));
    }
}
