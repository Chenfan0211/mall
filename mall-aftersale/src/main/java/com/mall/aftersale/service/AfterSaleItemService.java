package com.mall.aftersale.service;

import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.aftersale.vo.AfterSaleItemQueryVO;
import com.mall.common.page.PageResult;

public interface AfterSaleItemService {

    PageResult<AfterSaleItemDTO> pageItems(AfterSaleItemQueryVO query);

    AfterSaleItemDTO getItem(Long id);
}
