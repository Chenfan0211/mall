package com.mall.user.service;

import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.user.vo.UserAfterSaleApplyVO;
import com.mall.api.user.dto.UserReturnRecordDTO;
import com.mall.api.user.vo.UserAfterSaleQueryVO;
import com.mall.common.page.PageResult;
import java.util.List;

public interface UserAfterSaleService {

    PageResult<AfterSaleDTO> pageAfterSales(UserAfterSaleQueryVO query);

    AfterSaleDTO applyAfterSale(UserAfterSaleApplyVO request);

    AfterSaleDTO getAfterSale(Long id, Long userId);

    List<AfterSaleItemDTO> listAfterSaleItems(Long afterSaleId, Long userId);

    List<UserReturnRecordDTO> listReturnRecords(Long afterSaleId, Long userId);
}
