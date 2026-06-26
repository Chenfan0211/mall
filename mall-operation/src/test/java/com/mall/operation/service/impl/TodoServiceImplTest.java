package com.mall.operation.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.operation.dto.TodoDTO;
import com.mall.api.operation.vo.TodoQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.operation.convert.OperationConvert;
import com.mall.operation.entity.OpTodo;
import com.mall.operation.mapper.OpTodoMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private OpTodoMapper todoMapper;

    @Mock
    private OperationConvert operationConvert;

    @InjectMocks
    private TodoServiceImpl todoService;

    @Test
    void pageTodos_success() {
        final TodoQueryVO query = new TodoQueryVO();
        query.setStatus(10L);
        final OpTodo todo = new OpTodo();
        todo.setId(791001L);
        todo.setTitle("采购单待审核");
        final Page<OpTodo> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(todo));
        final TodoDTO todoDTO = new TodoDTO();
        todoDTO.setId(791001L);
        todoDTO.setTitle("采购单待审核");
        when(todoMapper.selectPage(any(), any())).thenReturn(page);
        when(operationConvert.toTodoDTO(todo)).thenReturn(todoDTO);

        final PageResult<TodoDTO> result = todoService.pageTodos(query);

        assertEquals(1L, result.getTotal());
        assertEquals("采购单待审核", result.getList().get(0).getTitle());
    }

    @Test
    void getTodo_notFound_throwNotFound() {
        when(todoMapper.selectById(791999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> todoService.getTodo(791999L));
    }
}
