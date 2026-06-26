package com.mall.trade.convert;

import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.trade.dto.OrderStatusLogDTO;
import com.mall.api.trade.dto.StockoutRecordDTO;
import com.mall.trade.entity.OrdFulfillmentTrack;
import com.mall.trade.entity.OrdOrder;
import com.mall.trade.entity.OrdOrderItem;
import com.mall.trade.entity.OrdOrderStatusLog;
import com.mall.trade.entity.OrdStockoutRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeConvert {

    OrderDTO toOrderDTO(OrdOrder entity);

    OrderItemDTO toOrderItemDTO(OrdOrderItem entity);

    OrderStatusLogDTO toOrderStatusLogDTO(OrdOrderStatusLog entity);

    FulfillmentTrackDTO toFulfillmentTrackDTO(OrdFulfillmentTrack entity);

    StockoutRecordDTO toStockoutRecordDTO(OrdStockoutRecord entity);
}
