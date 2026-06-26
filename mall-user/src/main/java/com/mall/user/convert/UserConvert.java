package com.mall.user.convert;

import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.dto.ProductDTO;
import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.user.dto.UserCartItemDTO;
import com.mall.api.user.dto.UserCommentDTO;
import com.mall.api.user.dto.UserMessageDTO;
import com.mall.api.user.dto.UserReturnRecordDTO;
import com.mall.api.user.dto.UserStationDTO;
import com.mall.user.entity.AfsAfterSale;
import com.mall.user.entity.AfsAfterSaleItem;
import com.mall.user.entity.AfsReturnRecord;
import com.mall.user.entity.MsgRecord;
import com.mall.user.entity.OrdCart;
import com.mall.user.entity.OrdFulfillmentTrack;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.OrdOrderItem;
import com.mall.user.entity.PayTrade;
import com.mall.user.entity.PrdCategory;
import com.mall.user.entity.PrdProduct;
import com.mall.user.entity.UsrComment;
import com.mall.user.entity.UsrStation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConvert {

    CategoryDTO toCategoryDTO(PrdCategory entity);

    ProductDTO toProductDTO(PrdProduct entity);

    UserStationDTO toStationDTO(UsrStation entity);

    UserCartItemDTO toCartItemDTO(OrdCart entity);

    OrderDTO toOrderDTO(OrdOrder entity);

    OrderItemDTO toOrderItemDTO(OrdOrderItem entity);

    PaymentTradeDTO toPaymentTradeDTO(PayTrade entity);

    FulfillmentTrackDTO toFulfillmentTrackDTO(OrdFulfillmentTrack entity);

    AfterSaleDTO toAfterSaleDTO(AfsAfterSale entity);

    AfterSaleItemDTO toAfterSaleItemDTO(AfsAfterSaleItem entity);

    UserReturnRecordDTO toReturnRecordDTO(AfsReturnRecord entity);

    UserCommentDTO toCommentDTO(UsrComment entity);

    UserMessageDTO toMessageDTO(MsgRecord entity);
}
