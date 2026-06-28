package com.mall.station.controller;

import com.mall.api.operation.dto.ExceptionRecordDTO;
import com.mall.api.station.dto.StationWorkbenchDTO;
import com.mall.api.station.vo.StationArrivalConfirmVO;
import com.mall.api.station.vo.StationDeliveryQueryVO;
import com.mall.api.station.vo.StationOrderQueryVO;
import com.mall.api.station.vo.StationPickupConfirmVO;
import com.mall.api.station.vo.StationWorkbenchQueryVO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.wms.dto.DeliveryOrderDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.api.wms.dto.ReturnHandoverDTO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.station.service.StationOrderService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/station/workbench")
public class StationOrderController {

    private final StationOrderService stationOrderService;

    public StationOrderController(final StationOrderService stationOrderService) {
        this.stationOrderService = stationOrderService;
    }

    @GetMapping("/summary")
    public Result<StationWorkbenchDTO> getWorkbench(@Valid final StationWorkbenchQueryVO query) {
        return Result.success(stationOrderService.getWorkbench(query));
    }

    @GetMapping("/orders")
    public Result<PageResult<OrderDTO>> pageOrders(@Valid final StationOrderQueryVO query) {
        return Result.success(stationOrderService.pageOrders(query));
    }

    @GetMapping("/orders/{id}")
    public Result<OrderDTO> getOrder(@PathVariable final Long id) {
        return Result.success(stationOrderService.getOrder(id));
    }

    @GetMapping("/order-items")
    public Result<PageResult<OrderItemDTO>> pageOrderItems(@Valid final StationOrderQueryVO query) {
        return Result.success(stationOrderService.pageOrderItems(query));
    }

    @GetMapping("/order-items/{id}")
    public Result<OrderItemDTO> getOrderItem(@PathVariable final Long id) {
        return Result.success(stationOrderService.getOrderItem(id));
    }

    @PostMapping("/order-items/{id}/pickup-confirm")
    public Result<OrderItemDTO> confirmPickup(
            @PathVariable final Long id,
            @Valid @RequestBody(required = false) final StationPickupConfirmVO request) {
        return Result.success(stationOrderService.confirmPickup(id, request));
    }

    @GetMapping("/deliveries")
    public Result<PageResult<DeliveryOrderDTO>> pageDeliveryOrders(@Valid final StationDeliveryQueryVO query) {
        return Result.success(stationOrderService.pageDeliveryOrders(query));
    }

    @GetMapping("/deliveries/{id}")
    public Result<DeliveryOrderDTO> getDeliveryOrder(@PathVariable final Long id) {
        return Result.success(stationOrderService.getDeliveryOrder(id));
    }

    @GetMapping("/delivery-stations")
    public Result<PageResult<DeliveryStationDTO>> pageDeliveryStations(@Valid final StationDeliveryQueryVO query) {
        return Result.success(stationOrderService.pageDeliveryStations(query));
    }

    @GetMapping("/delivery-stations/{id}")
    public Result<DeliveryStationDTO> getDeliveryStation(@PathVariable final Long id) {
        return Result.success(stationOrderService.getDeliveryStation(id));
    }

    @PostMapping("/delivery-stations/{id}/arrival-confirm")
    public Result<DeliveryStationDTO> confirmArrival(
            @PathVariable final Long id,
            @Valid @RequestBody(required = false) final StationArrivalConfirmVO request) {
        return Result.success(stationOrderService.confirmArrival(id, request));
    }

    @GetMapping("/returns")
    public Result<PageResult<ReturnHandoverDTO>> pageReturnHandovers(@Valid final StationDeliveryQueryVO query) {
        return Result.success(stationOrderService.pageReturnHandovers(query));
    }

    @GetMapping("/returns/{id}")
    public Result<ReturnHandoverDTO> getReturnHandover(@PathVariable final Long id) {
        return Result.success(stationOrderService.getReturnHandover(id));
    }

    @GetMapping("/exceptions")
    public Result<PageResult<ExceptionRecordDTO>> pageExceptions(@Valid final StationDeliveryQueryVO query) {
        return Result.success(stationOrderService.pageExceptions(query));
    }
}
