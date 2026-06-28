package com.mall.wms.controller;

import com.mall.api.wms.dto.InboundItemDTO;
import com.mall.api.wms.dto.InboundOrderDTO;
import com.mall.api.wms.dto.PutawayTaskDTO;
import com.mall.api.wms.dto.QualityFileDTO;
import com.mall.api.wms.dto.ReceiveRecordDTO;
import com.mall.api.wms.vo.WmsInboundQueryVO;
import com.mall.common.page.PageResult;
import com.mall.common.result.Result;
import com.mall.wms.service.WmsInboundService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/wms/inbound")
public class WmsInboundController {

    private final WmsInboundService wmsInboundService;

    public WmsInboundController(final WmsInboundService wmsInboundService) {
        this.wmsInboundService = wmsInboundService;
    }

    @GetMapping("/orders")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:inbound:view')")
    public Result<PageResult<InboundOrderDTO>> pageInboundOrders(@Valid final WmsInboundQueryVO query) {
        return Result.success(wmsInboundService.pageInboundOrders(query));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:inbound:view')")
    public Result<InboundOrderDTO> getInboundOrder(@PathVariable final Long id) {
        return Result.success(wmsInboundService.getInboundOrder(id));
    }

    @GetMapping("/items")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:inbound-item:view')")
    public Result<PageResult<InboundItemDTO>> pageInboundItems(@Valid final WmsInboundQueryVO query) {
        return Result.success(wmsInboundService.pageInboundItems(query));
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:inbound-item:view')")
    public Result<InboundItemDTO> getInboundItem(@PathVariable final Long id) {
        return Result.success(wmsInboundService.getInboundItem(id));
    }

    @GetMapping("/receive-records")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:receive:view')")
    public Result<PageResult<ReceiveRecordDTO>> pageReceiveRecords(@Valid final WmsInboundQueryVO query) {
        return Result.success(wmsInboundService.pageReceiveRecords(query));
    }

    @GetMapping("/receive-records/{id}")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:receive:view')")
    public Result<ReceiveRecordDTO> getReceiveRecord(@PathVariable final Long id) {
        return Result.success(wmsInboundService.getReceiveRecord(id));
    }

    @GetMapping("/quality-files")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:quality:view')")
    public Result<PageResult<QualityFileDTO>> pageQualityFiles(@Valid final WmsInboundQueryVO query) {
        return Result.success(wmsInboundService.pageQualityFiles(query));
    }

    @GetMapping("/quality-files/{id}")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:quality:view')")
    public Result<QualityFileDTO> getQualityFile(@PathVariable final Long id) {
        return Result.success(wmsInboundService.getQualityFile(id));
    }

    @GetMapping("/putaway-tasks")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:putaway:view')")
    public Result<PageResult<PutawayTaskDTO>> pagePutawayTasks(@Valid final WmsInboundQueryVO query) {
        return Result.success(wmsInboundService.pagePutawayTasks(query));
    }

    @GetMapping("/putaway-tasks/{id}")
    @PreAuthorize("@portalAuthorization.isPortal('warehouse-h5') or hasAuthority('wms:putaway:view')")
    public Result<PutawayTaskDTO> getPutawayTask(@PathVariable final Long id) {
        return Result.success(wmsInboundService.getPutawayTask(id));
    }
}
