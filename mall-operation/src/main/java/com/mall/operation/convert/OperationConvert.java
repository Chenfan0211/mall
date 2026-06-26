package com.mall.operation.convert;

import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.operation.dto.TodoDTO;
import com.mall.operation.entity.OpExceptionRecord;
import com.mall.operation.entity.OpTodo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationConvert {

    TodoDTO toTodoDTO(OpTodo entity);

    ExceptionRecordDTO toExceptionRecordDTO(OpExceptionRecord entity);
}
