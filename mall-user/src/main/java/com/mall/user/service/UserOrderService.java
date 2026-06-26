package com.mall.user.service;

import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.user.vo.UserOrderCancelVO;
import com.mall.api.user.vo.UserOrderQueryVO;
import com.mall.api.user.vo.UserOrderSubmitVO;
import com.mall.api.user.vo.UserPaymentCreateVO;
import com.mall.common.page.PageResult;
import java.util.List;

public interface UserOrderService {

    PageResult<OrderDTO> pageOrders(UserOrderQueryVO query);

    OrderDTO submitOrder(UserOrderSubmitVO request);

    OrderDTO getOrder(Long id, Long userId);

    OrderDTO cancelWaitPayOrder(Long id, UserOrderCancelVO request);

    PageResult<OrderItemDTO> pageOrderItems(UserOrderQueryVO query);

    List<FulfillmentTrackDTO> listFulfillmentTracks(Long orderId, Long userId);

    PaymentTradeDTO createPaymentTrade(Long orderId, UserPaymentCreateVO request);
}
