package com.mall.supplier.service;

import com.mall.api.supplier.dto.PurchaseAuditLogDTO;
import com.mall.api.supplier.dto.PurchaseItemDTO;
import com.mall.api.supplier.dto.PurchaseOrderDTO;
import com.mall.api.supplier.dto.PurchaseSourceLogDTO;
import com.mall.api.supplier.vo.PurchaseItemQueryVO;
import com.mall.api.supplier.vo.PurchaseOrderQueryVO;
import com.mall.common.page.PageResult;
import java.util.List;

public interface PurchaseService {

    PageResult<PurchaseOrderDTO> pagePurchaseOrders(PurchaseOrderQueryVO query);

    PurchaseOrderDTO getPurchaseOrder(Long id);

    PageResult<PurchaseItemDTO> pagePurchaseItems(PurchaseItemQueryVO query);

    PurchaseItemDTO getPurchaseItem(Long id);

    List<PurchaseAuditLogDTO> listAuditLogs(Long purchaseId);

    List<PurchaseSourceLogDTO> listSourceLogs(Long purchaseId);
}
