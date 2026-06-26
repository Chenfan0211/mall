package com.mall.supplier.service;

import com.mall.api.supplier.dto.SupplierContactDTO;
import com.mall.api.supplier.dto.SupplierContractDTO;
import com.mall.api.supplier.dto.SupplierDTO;
import com.mall.api.supplier.dto.SupplierQualificationDTO;
import com.mall.api.supplier.vo.SupplierContactQueryVO;
import com.mall.api.supplier.vo.SupplierContractQueryVO;
import com.mall.api.supplier.vo.SupplierQualificationQueryVO;
import com.mall.api.supplier.vo.SupplierQueryVO;
import com.mall.common.page.PageResult;

public interface SupplierProfileService {

    PageResult<SupplierDTO> pageSuppliers(SupplierQueryVO query);

    SupplierDTO getSupplier(Long id);

    PageResult<SupplierContactDTO> pageContacts(SupplierContactQueryVO query);

    SupplierContactDTO getContact(Long id);

    PageResult<SupplierQualificationDTO> pageQualifications(SupplierQualificationQueryVO query);

    SupplierQualificationDTO getQualification(Long id);

    PageResult<SupplierContractDTO> pageContracts(SupplierContractQueryVO query);

    SupplierContractDTO getContract(Long id);
}
