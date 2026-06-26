package com.mall.aftersale.service;

import com.mall.api.aftersale.dto.AfterSaleAuditLogDTO;
import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.vo.AfterSaleQueryVO;
import com.mall.common.page.PageResult;
import java.util.List;

public interface AfterSaleService {

    PageResult<AfterSaleDTO> pageAfterSales(AfterSaleQueryVO query);

    AfterSaleDTO getAfterSale(Long id);

    List<AfterSaleAuditLogDTO> listAuditLogs(Long afterSaleId);
}
