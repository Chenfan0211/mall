package com.mall.supplier.convert;

import com.mall.api.supplier.dto.DeliveryItemDTO;
import com.mall.api.supplier.dto.DeliveryOrderDTO;
import com.mall.api.supplier.dto.PurchaseAuditLogDTO;
import com.mall.api.supplier.dto.PurchaseItemDTO;
import com.mall.api.supplier.dto.PurchaseOrderDTO;
import com.mall.api.supplier.dto.PurchaseSourceLogDTO;
import com.mall.api.supplier.dto.SupplierContactDTO;
import com.mall.api.supplier.dto.SupplierContractDTO;
import com.mall.api.supplier.dto.SupplierDTO;
import com.mall.api.supplier.dto.SupplierQualificationDTO;
import com.mall.supplier.entity.PurDeliveryItem;
import com.mall.supplier.entity.PurDeliveryOrder;
import com.mall.supplier.entity.PurPurchaseAuditLog;
import com.mall.supplier.entity.PurPurchaseItem;
import com.mall.supplier.entity.PurPurchaseOrder;
import com.mall.supplier.entity.PurPurchaseSourceLog;
import com.mall.supplier.entity.SupContract;
import com.mall.supplier.entity.SupQualification;
import com.mall.supplier.entity.SupSupplier;
import com.mall.supplier.entity.SupSupplierContact;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierConvert {

    SupplierDTO toSupplierDTO(SupSupplier entity);

    SupplierContactDTO toSupplierContactDTO(SupSupplierContact entity);

    SupplierQualificationDTO toSupplierQualificationDTO(SupQualification entity);

    SupplierContractDTO toSupplierContractDTO(SupContract entity);

    PurchaseOrderDTO toPurchaseOrderDTO(PurPurchaseOrder entity);

    PurchaseItemDTO toPurchaseItemDTO(PurPurchaseItem entity);

    PurchaseAuditLogDTO toPurchaseAuditLogDTO(PurPurchaseAuditLog entity);

    PurchaseSourceLogDTO toPurchaseSourceLogDTO(PurPurchaseSourceLog entity);

    DeliveryOrderDTO toDeliveryOrderDTO(PurDeliveryOrder entity);

    DeliveryItemDTO toDeliveryItemDTO(PurDeliveryItem entity);
}
