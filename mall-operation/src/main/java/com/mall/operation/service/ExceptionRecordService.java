package com.mall.operation.service;

import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.operation.vo.ExceptionRecordQueryVO;
import com.mall.common.page.PageResult;

public interface ExceptionRecordService {

    PageResult<ExceptionRecordDTO> pageExceptionRecords(ExceptionRecordQueryVO query);

    ExceptionRecordDTO getExceptionRecord(Long id);
}
