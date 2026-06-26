package com.mall.sale.service;

import com.mall.api.sale.dto.PeriodSnapshotDTO;
import com.mall.api.sale.dto.PublishPeriodDTO;
import com.mall.api.sale.vo.PublishPeriodQueryVO;
import com.mall.common.page.PageResult;
import java.util.List;

public interface PublishPeriodService {

    PageResult<PublishPeriodDTO> pagePeriods(PublishPeriodQueryVO query);

    PublishPeriodDTO getPeriod(Long id);

    List<PeriodSnapshotDTO> listSnapshots(Long periodId);
}
