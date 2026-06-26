package com.mall.trade.service;

import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.trade.vo.OrderItemQueryVO;
import com.mall.common.page.PageResult;

public interface OrderItemService {

    PageResult<OrderItemDTO> pageOrderItems(OrderItemQueryVO query);

    OrderItemDTO getOrderItem(Long id);
}
