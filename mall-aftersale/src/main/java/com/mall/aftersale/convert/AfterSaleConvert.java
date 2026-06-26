package com.mall.aftersale.convert;

import com.mall.aftersale.entity.AfsAfterSale;
import com.mall.aftersale.entity.AfsAfterSaleItem;
import com.mall.aftersale.entity.AfsAuditLog;
import com.mall.aftersale.entity.AfsReturnRecord;
import com.mall.api.aftersale.dto.AfterSaleAuditLogDTO;
import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.api.aftersale.dto.AfterSaleReturnRecordDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AfterSaleConvert {

    AfterSaleDTO toAfterSaleDTO(AfsAfterSale entity);

    AfterSaleItemDTO toAfterSaleItemDTO(AfsAfterSaleItem entity);

    AfterSaleAuditLogDTO toAfterSaleAuditLogDTO(AfsAuditLog entity);

    AfterSaleReturnRecordDTO toAfterSaleReturnRecordDTO(AfsReturnRecord entity);
}
