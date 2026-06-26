package com.mall.wms.service;

import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.api.wms.dto.DriverSignRecordDTO;
import com.mall.api.wms.dto.LoadingItemDTO;
import com.mall.api.wms.dto.LoadingOrderDTO;
import com.mall.api.wms.dto.OutboundOrderDTO;
import com.mall.api.wms.dto.PickItemDTO;
import com.mall.api.wms.dto.PickTaskDTO;
import com.mall.api.wms.dto.ReturnHandoverDTO;
import com.mall.api.wms.dto.WaveBatchDTO;
import com.mall.api.wms.dto.WmsOperationLogDTO;
import com.mall.api.wms.vo.WmsOutboundQueryVO;
import com.mall.common.page.PageResult;

public interface WmsOutboundService {

    PageResult<WaveBatchDTO> pageWaveBatches(WmsOutboundQueryVO query);

    WaveBatchDTO getWaveBatch(Long id);

    PageResult<PickTaskDTO> pagePickTasks(WmsOutboundQueryVO query);

    PickTaskDTO getPickTask(Long id);

    PageResult<PickItemDTO> pagePickItems(WmsOutboundQueryVO query);

    PickItemDTO getPickItem(Long id);

    PageResult<LoadingOrderDTO> pageLoadingOrders(WmsOutboundQueryVO query);

    LoadingOrderDTO getLoadingOrder(Long id);

    PageResult<LoadingItemDTO> pageLoadingItems(WmsOutboundQueryVO query);

    LoadingItemDTO getLoadingItem(Long id);

    PageResult<OutboundOrderDTO> pageOutboundOrders(WmsOutboundQueryVO query);

    OutboundOrderDTO getOutboundOrder(Long id);

    PageResult<DeliveryOrderDTO> pageDeliveryOrders(WmsOutboundQueryVO query);

    DeliveryOrderDTO getDeliveryOrder(Long id);

    PageResult<DeliveryStationDTO> pageDeliveryStations(WmsOutboundQueryVO query);

    DeliveryStationDTO getDeliveryStation(Long id);

    PageResult<DriverSignRecordDTO> pageDriverSignRecords(WmsOutboundQueryVO query);

    DriverSignRecordDTO getDriverSignRecord(Long id);

    PageResult<ReturnHandoverDTO> pageReturnHandovers(WmsOutboundQueryVO query);

    ReturnHandoverDTO getReturnHandover(Long id);

    PageResult<WmsOperationLogDTO> pageOperationLogs(WmsOutboundQueryVO query);

    WmsOperationLogDTO getOperationLog(Long id);
}
