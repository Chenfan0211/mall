package com.mall.trade.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.trade.convert.TradeConvert;
import com.mall.trade.entity.OrdFulfillmentTrack;
import com.mall.trade.mapper.OrdFulfillmentTrackMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FulfillmentTrackServiceImplTest {

    @Mock
    private OrdFulfillmentTrackMapper fulfillmentTrackMapper;

    @Mock
    private TradeConvert tradeConvert;

    @InjectMocks
    private FulfillmentTrackServiceImpl fulfillmentTrackService;

    @Test
    void listTracks_success() {
        final OrdFulfillmentTrack track = new OrdFulfillmentTrack();
        track.setId(764003L);
        track.setNodeName("待自提");
        final FulfillmentTrackDTO trackDTO = new FulfillmentTrackDTO();
        trackDTO.setId(764003L);
        trackDTO.setNodeName("待自提");
        when(fulfillmentTrackMapper.selectList(any())).thenReturn(List.of(track));
        when(tradeConvert.toFulfillmentTrackDTO(track)).thenReturn(trackDTO);

        final List<FulfillmentTrackDTO> result = fulfillmentTrackService.listTracks(null);

        assertEquals(1, result.size());
        assertEquals("待自提", result.get(0).getNodeName());
    }
}
