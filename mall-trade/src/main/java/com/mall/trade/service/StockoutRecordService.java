package com.mall.trade.service;

import com.mall.api.trade.dto.StockoutRecordDTO;
import com.mall.api.trade.vo.StockoutRecordQueryVO;
import com.mall.common.page.PageResult;

public interface StockoutRecordService {

    PageResult<StockoutRecordDTO> pageStockouts(StockoutRecordQueryVO query);

    StockoutRecordDTO getStockout(Long id);
}
