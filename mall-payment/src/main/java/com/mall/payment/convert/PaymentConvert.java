package com.mall.payment.convert;

import com.mall.api.payment.dto.FundFlowDTO;
import com.mall.api.payment.dto.PaymentTradeDTO;
import com.mall.api.payment.dto.RefundTradeDTO;
import com.mall.payment.entity.PayFundFlow;
import com.mall.payment.entity.PayRefundTrade;
import com.mall.payment.entity.PayTrade;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentConvert {

    PaymentTradeDTO toPaymentTradeDTO(PayTrade entity);

    RefundTradeDTO toRefundTradeDTO(PayRefundTrade entity);

    FundFlowDTO toFundFlowDTO(PayFundFlow entity);
}
