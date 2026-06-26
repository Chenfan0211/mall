package com.mall.aftersale.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.aftersale.convert.AfterSaleConvert;
import com.mall.aftersale.entity.AfsAfterSale;
import com.mall.aftersale.mapper.AfsAfterSaleMapper;
import com.mall.aftersale.mapper.AfsAuditLogMapper;
import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AfterSaleServiceImplTest {

    @Mock
    private AfsAfterSaleMapper afterSaleMapper;

    @Mock
    private AfsAuditLogMapper auditLogMapper;

    @Mock
    private AfterSaleConvert afterSaleConvert;

    @InjectMocks
    private AfterSaleServiceImpl afterSaleService;

    @Test
    void pageAfterSales_success() {
        final AfsAfterSale afterSale = new AfsAfterSale();
        afterSale.setId(767001L);
        afterSale.setAfterSaleNo("AFS_TEST_WAIT_AUDIT");
        final AfterSaleDTO afterSaleDTO = new AfterSaleDTO();
        afterSaleDTO.setId(767001L);
        afterSaleDTO.setAfterSaleNo("AFS_TEST_WAIT_AUDIT");
        final Page<AfsAfterSale> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(afterSale));
        when(afterSaleMapper.selectPage(any(), any())).thenReturn(page);
        when(afterSaleConvert.toAfterSaleDTO(afterSale)).thenReturn(afterSaleDTO);

        final PageResult<AfterSaleDTO> result = afterSaleService.pageAfterSales(null);

        assertEquals(1L, result.getTotal());
        assertEquals("AFS_TEST_WAIT_AUDIT", result.getList().get(0).getAfterSaleNo());
    }

    @Test
    void getAfterSale_notFound_throwNotFound() {
        when(afterSaleMapper.selectById(767999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> afterSaleService.getAfterSale(767999L));
    }
}
