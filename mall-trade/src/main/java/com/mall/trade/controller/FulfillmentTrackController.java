package com.mall.trade.controller;

import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.vo.FulfillmentTrackQueryVO;
import com.mall.common.result.Result;
import com.mall.trade.service.FulfillmentTrackService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/trade/fulfillment-tracks")
public class FulfillmentTrackController {

    private final FulfillmentTrackService fulfillmentTrackService;

    public FulfillmentTrackController(final FulfillmentTrackService fulfillmentTrackService) {
        this.fulfillmentTrackService = fulfillmentTrackService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('trade:fulfillment:view')")
    public Result<List<FulfillmentTrackDTO>> listTracks(@Valid final FulfillmentTrackQueryVO query) {
        return Result.success(fulfillmentTrackService.listTracks(query));
    }
}
