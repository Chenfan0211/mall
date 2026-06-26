package com.mall.supplier.service;

import com.mall.api.supplier.dto.SupplierWorkbenchDTO;
import com.mall.api.supplier.vo.SupplierWorkbenchQueryVO;

public interface SupplierWorkbenchService {

    SupplierWorkbenchDTO getWorkbench(SupplierWorkbenchQueryVO query);
}
