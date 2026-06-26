package com.mall.station.service;

import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.station.vo.StationArrivalConfirmVO;
import com.mall.api.station.vo.StationPickupConfirmVO;
import com.mall.api.station.dto.StationWorkbenchDTO;
import com.mall.api.station.vo.StationDeliveryQueryVO;
import com.mall.api.station.vo.StationOrderQueryVO;
import com.mall.api.station.vo.StationWorkbenchQueryVO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.api.wms.dto.ReturnHandoverDTO;
import com.mall.common.page.PageResult;

public interface StationOrderService {

    StationWorkbenchDTO getWorkbench(StationWorkbenchQueryVO query);

    PageResult<OrderDTO> pageOrders(StationOrderQueryVO query);

    OrderDTO getOrder(Long id);

    PageResult<OrderItemDTO> pageOrderItems(StationOrderQueryVO query);

    OrderItemDTO getOrderItem(Long id);

    OrderItemDTO confirmPickup(Long id, StationPickupConfirmVO request);

    PageResult<DeliveryOrderDTO> pageDeliveryOrders(StationDeliveryQueryVO query);

    DeliveryOrderDTO getDeliveryOrder(Long id);

    PageResult<DeliveryStationDTO> pageDeliveryStations(StationDeliveryQueryVO query);

    DeliveryStationDTO getDeliveryStation(Long id);

    DeliveryStationDTO confirmArrival(Long id, StationArrivalConfirmVO request);

    PageResult<ReturnHandoverDTO> pageReturnHandovers(StationDeliveryQueryVO query);

    ReturnHandoverDTO getReturnHandover(Long id);

    PageResult<ExceptionRecordDTO> pageExceptions(StationDeliveryQueryVO query);
}
