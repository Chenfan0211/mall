package com.mall.operation.controller;

import com.mall.api.operation.dto.TodoDTO;
import com.mall.api.operation.vo.TodoQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.operation.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/operation/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(final TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('operation:todo:view')")
    public Result<PageResult<TodoDTO>> pageTodos(@Valid final TodoQueryVO query) {
        return Result.success(todoService.pageTodos(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('operation:todo:view')")
    public Result<TodoDTO> getTodo(@PathVariable final Long id) {
        return Result.success(todoService.getTodo(id));
    }
}
