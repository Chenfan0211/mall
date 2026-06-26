package com.mall.wms.convert;

import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.api.wms.dto.DriverDTO;
import com.mall.api.wms.dto.DriverSignRecordDTO;
import com.mall.api.wms.dto.InboundItemDTO;
import com.mall.api.wms.dto.InboundOrderDTO;
import com.mall.api.wms.dto.InventoryDTO;
import com.mall.api.wms.dto.InventoryLockDTO;
import com.mall.api.wms.dto.InventoryLogDTO;
import com.mall.api.wms.dto.LineDTO;
import com.mall.api.wms.dto.LineStationDTO;
import com.mall.api.wms.dto.LoadingItemDTO;
import com.mall.api.wms.dto.LoadingOrderDTO;
import com.mall.api.wms.dto.LocationDTO;
import com.mall.api.wms.dto.OutboundOrderDTO;
import com.mall.api.wms.dto.PickItemDTO;
import com.mall.api.wms.dto.PickTaskDTO;
import com.mall.api.wms.dto.PutawayTaskDTO;
import com.mall.api.wms.dto.QualityFileDTO;
import com.mall.api.wms.dto.ReceiveRecordDTO;
import com.mall.api.wms.dto.ReturnHandoverDTO;
import com.mall.api.wms.dto.StocktakeOrderDTO;
import com.mall.api.wms.dto.WarehouseDTO;
import com.mall.api.wms.dto.WaveBatchDTO;
import com.mall.api.wms.dto.WmsOperationLogDTO;
import com.mall.api.wms.dto.ZoneDTO;
import com.mall.wms.entity.WmsDeliveryOrder;
import com.mall.wms.entity.WmsDeliveryStation;
import com.mall.wms.entity.WmsDriver;
import com.mall.wms.entity.WmsDriverSignRecord;
import com.mall.wms.entity.WmsInboundItem;
import com.mall.wms.entity.WmsInboundOrder;
import com.mall.wms.entity.WmsInventory;
import com.mall.wms.entity.WmsInventoryLock;
import com.mall.wms.entity.WmsInventoryLog;
import com.mall.wms.entity.WmsLine;
import com.mall.wms.entity.WmsLineStation;
import com.mall.wms.entity.WmsLoadingItem;
import com.mall.wms.entity.WmsLoadingOrder;
import com.mall.wms.entity.WmsLocation;
import com.mall.wms.entity.WmsOperationLog;
import com.mall.wms.entity.WmsOutboundOrder;
import com.mall.wms.entity.WmsPickItem;
import com.mall.wms.entity.WmsPickTask;
import com.mall.wms.entity.WmsPutawayTask;
import com.mall.wms.entity.WmsQualityFile;
import com.mall.wms.entity.WmsReceiveRecord;
import com.mall.wms.entity.WmsReturnHandover;
import com.mall.wms.entity.WmsStocktakeOrder;
import com.mall.wms.entity.WmsWarehouse;
import com.mall.wms.entity.WmsWaveBatch;
import com.mall.wms.entity.WmsZone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WmsConvert {

    WarehouseDTO toWarehouseDTO(WmsWarehouse entity);

    ZoneDTO toZoneDTO(WmsZone entity);

    LocationDTO toLocationDTO(WmsLocation entity);

    LineDTO toLineDTO(WmsLine entity);

    LineStationDTO toLineStationDTO(WmsLineStation entity);

    DriverDTO toDriverDTO(WmsDriver entity);

    InboundOrderDTO toInboundOrderDTO(WmsInboundOrder entity);

    InboundItemDTO toInboundItemDTO(WmsInboundItem entity);

    ReceiveRecordDTO toReceiveRecordDTO(WmsReceiveRecord entity);

    QualityFileDTO toQualityFileDTO(WmsQualityFile entity);

    PutawayTaskDTO toPutawayTaskDTO(WmsPutawayTask entity);

    InventoryDTO toInventoryDTO(WmsInventory entity);

    InventoryLockDTO toInventoryLockDTO(WmsInventoryLock entity);

    InventoryLogDTO toInventoryLogDTO(WmsInventoryLog entity);

    WaveBatchDTO toWaveBatchDTO(WmsWaveBatch entity);

    PickTaskDTO toPickTaskDTO(WmsPickTask entity);

    PickItemDTO toPickItemDTO(WmsPickItem entity);

    LoadingOrderDTO toLoadingOrderDTO(WmsLoadingOrder entity);

    LoadingItemDTO toLoadingItemDTO(WmsLoadingItem entity);

    OutboundOrderDTO toOutboundOrderDTO(WmsOutboundOrder entity);

    DeliveryOrderDTO toDeliveryOrderDTO(WmsDeliveryOrder entity);

    DeliveryStationDTO toDeliveryStationDTO(WmsDeliveryStation entity);

    DriverSignRecordDTO toDriverSignRecordDTO(WmsDriverSignRecord entity);

    ReturnHandoverDTO toReturnHandoverDTO(WmsReturnHandover entity);

    StocktakeOrderDTO toStocktakeOrderDTO(WmsStocktakeOrder entity);

    WmsOperationLogDTO toWmsOperationLogDTO(WmsOperationLog entity);
}
