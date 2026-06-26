package com.mall.sale.service;

import com.mall.api.sale.dto.PublishSkuDTO;
import com.mall.api.sale.vo.PublishSkuQueryVO;
import java.util.List;

public interface PublishSkuService {

    List<PublishSkuDTO> listPublishSkus(PublishSkuQueryVO query);
}
