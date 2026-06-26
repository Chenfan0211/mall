package com.mall.sale.convert;

import com.mall.api.sale.dto.PeriodSnapshotDTO;
import com.mall.api.sale.dto.PublishPeriodDTO;
import com.mall.api.sale.dto.PublishSkuDTO;
import com.mall.sale.entity.SalePeriodSnapshot;
import com.mall.sale.entity.SalePublishPeriod;
import com.mall.sale.entity.SalePublishSku;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleConvert {

    PublishPeriodDTO toPublishPeriodDTO(SalePublishPeriod entity);

    PublishSkuDTO toPublishSkuDTO(SalePublishSku entity);

    PeriodSnapshotDTO toPeriodSnapshotDTO(SalePeriodSnapshot entity);
}
