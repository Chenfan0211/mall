package com.mall.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.vo.FulfillmentTrackQueryVO;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdFulfillmentTrack;
import com.mall.trade.mapper.OrdFulfillmentTrackMapper;
import com.mall.trade.service.FulfillmentTrackService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FulfillmentTrackServiceImpl implements FulfillmentTrackService {

    private final OrdFulfillmentTrackMapper fulfillmentTrackMapper;
    private final TradeConvert tradeConvert;

    public FulfillmentTrackServiceImpl(
            final OrdFulfillmentTrackMapper fulfillmentTrackMapper,
            final TradeConvert tradeConvert) {
        this.fulfillmentTrackMapper = fulfillmentTrackMapper;
        this.tradeConvert = tradeConvert;
    }

    @Override
    public List<FulfillmentTrackDTO> listTracks(final FulfillmentTrackQueryVO query) {
        final FulfillmentTrackQueryVO safeQuery = query == null ? new FulfillmentTrackQueryVO() : query;
        final LambdaQueryWrapper<OrdFulfillmentTrack> wrapper = new LambdaQueryWrapper<OrdFulfillmentTrack>()
                .eq(safeQuery.getOrderId() != null, OrdFulfillmentTrack::getOrderId, safeQuery.getOrderId())
                .eq(safeQuery.getOrderItemId() != null, OrdFulfillmentTrack::getOrderItemId,
                        safeQuery.getOrderItemId())
                .eq(safeQuery.getNodeType() != null, OrdFulfillmentTrack::getNodeType, safeQuery.getNodeType())
                .eq(safeQuery.getStatus() != null, OrdFulfillmentTrack::getStatus, safeQuery.getStatus())
                .orderByAsc(OrdFulfillmentTrack::getCreateTime)
                .orderByAsc(OrdFulfillmentTrack::getId);
        return fulfillmentTrackMapper.selectList(wrapper).stream()
                .map(tradeConvert::toFulfillmentTrackDTO)
                .toList();
    }
}
