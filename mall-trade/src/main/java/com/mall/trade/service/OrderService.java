package com.mall.trade.service;

import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderStatusLogDTO;
import com.mall.api.trade.vo.OrderQueryVO;
import com.mall.common.page.PageResult;
import java.util.List;

public interface OrderService {

    PageResult<OrderDTO> pageOrders(OrderQueryVO query);

    OrderDTO getOrder(Long id);

    List<OrderStatusLogDTO> listStatusLogs(Long orderId);
}
