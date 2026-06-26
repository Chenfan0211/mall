package com.mall.wms.controller;

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
import com.mall.common.result.Result;
import com.mall.wms.service.WmsOutboundService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/wms/outbound")
public class WmsOutboundController {

    private final WmsOutboundService wmsOutboundService;

    public WmsOutboundController(final WmsOutboundService wmsOutboundService) {
        this.wmsOutboundService = wmsOutboundService;
    }

    @GetMapping("/waves")
    @PreAuthorize("hasAuthority('wms:wave:view')")
    public Result<PageResult<WaveBatchDTO>> pageWaveBatches(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageWaveBatches(query));
    }

    @GetMapping("/waves/{id}")
    @PreAuthorize("hasAuthority('wms:wave:view')")
    public Result<WaveBatchDTO> getWaveBatch(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getWaveBatch(id));
    }

    @GetMapping("/picks")
    @PreAuthorize("hasAuthority('wms:pick:view')")
    public Result<PageResult<PickTaskDTO>> pagePickTasks(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pagePickTasks(query));
    }

    @GetMapping("/picks/{id}")
    @PreAuthorize("hasAuthority('wms:pick:view')")
    public Result<PickTaskDTO> getPickTask(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getPickTask(id));
    }

    @GetMapping("/pick-items")
    @PreAuthorize("hasAuthority('wms:pick-item:view')")
    public Result<PageResult<PickItemDTO>> pagePickItems(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pagePickItems(query));
    }

    @GetMapping("/pick-items/{id}")
    @PreAuthorize("hasAuthority('wms:pick-item:view')")
    public Result<PickItemDTO> getPickItem(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getPickItem(id));
    }

    @GetMapping("/loading-orders")
    @PreAuthorize("hasAuthority('wms:loading:view')")
    public Result<PageResult<LoadingOrderDTO>> pageLoadingOrders(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageLoadingOrders(query));
    }

    @GetMapping("/loading-orders/{id}")
    @PreAuthorize("hasAuthority('wms:loading:view')")
    public Result<LoadingOrderDTO> getLoadingOrder(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getLoadingOrder(id));
    }

    @GetMapping("/loading-items")
    @PreAuthorize("hasAuthority('wms:loading-item:view')")
    public Result<PageResult<LoadingItemDTO>> pageLoadingItems(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageLoadingItems(query));
    }

    @GetMapping("/loading-items/{id}")
    @PreAuthorize("hasAuthority('wms:loading-item:view')")
    public Result<LoadingItemDTO> getLoadingItem(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getLoadingItem(id));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('wms:outbound:view')")
    public Result<PageResult<OutboundOrderDTO>> pageOutboundOrders(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageOutboundOrders(query));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('wms:outbound:view')")
    public Result<OutboundOrderDTO> getOutboundOrder(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getOutboundOrder(id));
    }

    @GetMapping("/deliveries")
    @PreAuthorize("hasAuthority('wms:delivery:view')")
    public Result<PageResult<DeliveryOrderDTO>> pageDeliveryOrders(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageDeliveryOrders(query));
    }

    @GetMapping("/deliveries/{id}")
    @PreAuthorize("hasAuthority('wms:delivery:view')")
    public Result<DeliveryOrderDTO> getDeliveryOrder(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getDeliveryOrder(id));
    }

    @GetMapping("/delivery-stations")
    @PreAuthorize("hasAuthority('wms:delivery-station:view')")
    public Result<PageResult<DeliveryStationDTO>> pageDeliveryStations(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageDeliveryStations(query));
    }

    @GetMapping("/delivery-stations/{id}")
    @PreAuthorize("hasAuthority('wms:delivery-station:view')")
    public Result<DeliveryStationDTO> getDeliveryStation(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getDeliveryStation(id));
    }

    @GetMapping("/driver-sign-records")
    @PreAuthorize("hasAuthority('wms:driver-sign:view')")
    public Result<PageResult<DriverSignRecordDTO>> pageDriverSignRecords(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageDriverSignRecords(query));
    }

    @GetMapping("/driver-sign-records/{id}")
    @PreAuthorize("hasAuthority('wms:driver-sign:view')")
    public Result<DriverSignRecordDTO> getDriverSignRecord(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getDriverSignRecord(id));
    }

    @GetMapping("/returns")
    @PreAuthorize("hasAuthority('wms:return:view')")
    public Result<PageResult<ReturnHandoverDTO>> pageReturnHandovers(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageReturnHandovers(query));
    }

    @GetMapping("/returns/{id}")
    @PreAuthorize("hasAuthority('wms:return:view')")
    public Result<ReturnHandoverDTO> getReturnHandover(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getReturnHandover(id));
    }

    @GetMapping("/operation-logs")
    @PreAuthorize("hasAuthority('wms:operation-log:view')")
    public Result<PageResult<WmsOperationLogDTO>> pageOperationLogs(@Valid final WmsOutboundQueryVO query) {
        return Result.success(wmsOutboundService.pageOperationLogs(query));
    }

    @GetMapping("/operation-logs/{id}")
    @PreAuthorize("hasAuthority('wms:operation-log:view')")
    public Result<WmsOperationLogDTO> getOperationLog(@PathVariable final Long id) {
        return Result.success(wmsOutboundService.getOperationLog(id));
    }
}
