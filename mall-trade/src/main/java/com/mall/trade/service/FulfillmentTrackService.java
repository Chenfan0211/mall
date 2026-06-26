package com.mall.trade.service;

import com.mall.api.trade.dto.FulfillmentTrackDTO;
import com.mall.api.trade.vo.FulfillmentTrackQueryVO;
import java.util.List;

public interface FulfillmentTrackService {

    List<FulfillmentTrackDTO> listTracks(FulfillmentTrackQueryVO query);
}
