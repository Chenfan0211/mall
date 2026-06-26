package com.mall.wms.service;

import com.mall.api.wms.dto.InboundItemDTO;
import com.mall.api.wms.dto.InboundOrderDTO;
import com.mall.api.wms.dto.PutawayTaskDTO;
import com.mall.api.wms.dto.QualityFileDTO;
import com.mall.api.wms.dto.ReceiveRecordDTO;
import com.mall.api.wms.vo.WmsInboundQueryVO;
import com.mall.common.page.PageResult;

public interface WmsInboundService {

    PageResult<InboundOrderDTO> pageInboundOrders(WmsInboundQueryVO query);

    InboundOrderDTO getInboundOrder(Long id);

    PageResult<InboundItemDTO> pageInboundItems(WmsInboundQueryVO query);

    InboundItemDTO getInboundItem(Long id);

    PageResult<ReceiveRecordDTO> pageReceiveRecords(WmsInboundQueryVO query);

    ReceiveRecordDTO getReceiveRecord(Long id);

    PageResult<QualityFileDTO> pageQualityFiles(WmsInboundQueryVO query);

    QualityFileDTO getQualityFile(Long id);

    PageResult<PutawayTaskDTO> pagePutawayTasks(WmsInboundQueryVO query);

    PutawayTaskDTO getPutawayTask(Long id);
}
