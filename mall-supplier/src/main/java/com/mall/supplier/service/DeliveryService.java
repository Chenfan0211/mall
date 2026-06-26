package com.mall.supplier.service;

import com.mall.api.supplier.dto.DeliveryItemDTO;
import com.mall.api.supplier.dto.DeliveryOrderDTO;
import com.mall.api.supplier.vo.DeliveryItemQueryVO;
import com.mall.api.supplier.vo.DeliveryOrderQueryVO;
import com.mall.common.page.PageResult;

public interface DeliveryService {

    PageResult<DeliveryOrderDTO> pageDeliveryOrders(DeliveryOrderQueryVO query);

    DeliveryOrderDTO getDeliveryOrder(Long id);

    PageResult<DeliveryItemDTO> pageDeliveryItems(DeliveryItemQueryVO query);

    DeliveryItemDTO getDeliveryItem(Long id);
}
