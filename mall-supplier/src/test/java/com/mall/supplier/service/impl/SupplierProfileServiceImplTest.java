package com.mall.supplier.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.supplier.dto.SupplierDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.supplier.convert.SupplierConvert;
import com.mall.supplier.entity.SupSupplier;
import com.mall.supplier.mapper.SupContractMapper;
import com.mall.supplier.mapper.SupQualificationMapper;
import com.mall.supplier.mapper.SupSupplierContactMapper;
import com.mall.supplier.mapper.SupSupplierMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierProfileServiceImplTest {

    @Mock
    private SupSupplierMapper supplierMapper;

    @Mock
    private SupSupplierContactMapper contactMapper;

    @Mock
    private SupQualificationMapper qualificationMapper;

    @Mock
    private SupContractMapper contractMapper;

    @Mock
    private SupplierConvert supplierConvert;

    @InjectMocks
    private SupplierProfileServiceImpl supplierProfileService;

    @Test
    void pageSuppliers_success() {
        final SupSupplier supplier = new SupSupplier();
        supplier.setId(710001L);
        supplier.setSupplierNo("SUP_TEST_FRESH");
        final SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setId(710001L);
        supplierDTO.setSupplierNo("SUP_TEST_FRESH");
        final Page<SupSupplier> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(supplier));
        when(supplierMapper.selectPage(any(), any())).thenReturn(page);
        when(supplierConvert.toSupplierDTO(supplier)).thenReturn(supplierDTO);

        final PageResult<SupplierDTO> result = supplierProfileService.pageSuppliers(null);

        assertEquals(1L, result.getTotal());
        assertEquals("SUP_TEST_FRESH", result.getList().get(0).getSupplierNo());
    }

    @Test
    void getSupplier_notFound_throwNotFound() {
        when(supplierMapper.selectById(719999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> supplierProfileService.getSupplier(719999L));
    }
}
