package com.mall.aftersale.service;

import com.mall.api.aftersale.dto.AfterSaleReturnRecordDTO;
import com.mall.api.aftersale.vo.AfterSaleReturnRecordQueryVO;
import com.mall.common.page.PageResult;

public interface AfterSaleReturnRecordService {

    PageResult<AfterSaleReturnRecordDTO> pageReturnRecords(AfterSaleReturnRecordQueryVO query);

    AfterSaleReturnRecordDTO getReturnRecord(Long id);
}
