package com.mall.wms.service;

import com.mall.api.wms.dto.InventoryDTO;
import com.mall.api.wms.dto.InventoryLockDTO;
import com.mall.api.wms.dto.InventoryLogDTO;
import com.mall.api.wms.dto.StocktakeOrderDTO;
import com.mall.api.wms.vo.WmsInventoryQueryVO;
import com.mall.common.page.PageResult;

public interface WmsInventoryService {

    PageResult<InventoryDTO> pageInventories(WmsInventoryQueryVO query);

    InventoryDTO getInventory(Long id);

    PageResult<InventoryLockDTO> pageInventoryLocks(WmsInventoryQueryVO query);

    InventoryLockDTO getInventoryLock(Long id);

    PageResult<InventoryLogDTO> pageInventoryLogs(WmsInventoryQueryVO query);

    InventoryLogDTO getInventoryLog(Long id);

    PageResult<StocktakeOrderDTO> pageStocktakeOrders(WmsInventoryQueryVO query);

    StocktakeOrderDTO getStocktakeOrder(Long id);
}
