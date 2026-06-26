package com.mall.station.convert;

import com.mall.api.finance.dto.CommissionDetailDTO;
import com.mall.api.finance.dto.FinanceAccountDTO;
import com.mall.api.finance.dto.FinanceAccountFlowDTO;
import com.mall.api.finance.dto.WithdrawApplyDTO;
import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.station.dto.LeaderDTO;
import com.mall.api.station.dto.StationDTO;
import com.mall.api.station.dto.StationLeaderDTO;
import com.mall.api.station.dto.StationLeaveDTO;
import com.mall.api.station.dto.StationMessageDTO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.api.wms.dto.ReturnHandoverDTO;
import com.mall.station.entity.FinAccount;
import com.mall.station.entity.FinAccountFlow;
import com.mall.station.entity.FinCommissionDetail;
import com.mall.station.entity.FinWithdrawApply;
import com.mall.station.entity.MsgRecord;
import com.mall.station.entity.OpExceptionRecord;
import com.mall.station.entity.OrdOrder;
import com.mall.station.entity.OrdOrderItem;
import com.mall.station.entity.UsrLeader;
import com.mall.station.entity.UsrStation;
import com.mall.station.entity.UsrStationLeader;
import com.mall.station.entity.UsrStationLeave;
import com.mall.station.entity.WmsDeliveryOrder;
import com.mall.station.entity.WmsDeliveryStation;
import com.mall.station.entity.WmsReturnHandover;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StationConvert {

    StationDTO toStationDTO(UsrStation entity);

    LeaderDTO toLeaderDTO(UsrLeader entity);

    StationLeaderDTO toStationLeaderDTO(UsrStationLeader entity);

    StationLeaveDTO toStationLeaveDTO(UsrStationLeave entity);

    StationMessageDTO toStationMessageDTO(MsgRecord entity);

    OrderDTO toOrderDTO(OrdOrder entity);

    OrderItemDTO toOrderItemDTO(OrdOrderItem entity);

    DeliveryOrderDTO toDeliveryOrderDTO(WmsDeliveryOrder entity);

    DeliveryStationDTO toDeliveryStationDTO(WmsDeliveryStation entity);

    ReturnHandoverDTO toReturnHandoverDTO(WmsReturnHandover entity);

    FinanceAccountDTO toFinanceAccountDTO(FinAccount entity);

    FinanceAccountFlowDTO toFinanceAccountFlowDTO(FinAccountFlow entity);

    CommissionDetailDTO toCommissionDetailDTO(FinCommissionDetail entity);

    WithdrawApplyDTO toWithdrawApplyDTO(FinWithdrawApply entity);

    ExceptionRecordDTO toExceptionRecordDTO(OpExceptionRecord entity);
}
