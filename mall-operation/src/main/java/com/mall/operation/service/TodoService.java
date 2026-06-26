package com.mall.operation.service;

import com.mall.api.operation.dto.TodoDTO;
import com.mall.api.operation.vo.TodoQueryVO;
import com.mall.common.page.PageResult;

public interface TodoService {

    PageResult<TodoDTO> pageTodos(TodoQueryVO query);

    TodoDTO getTodo(Long id);
}
