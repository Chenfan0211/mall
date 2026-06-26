package com.mall.aftersale.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.aftersale.convert.AfterSaleConvert;
import com.mall.aftersale.entity.AfsAfterSaleItem;
import com.mall.aftersale.mapper.AfsAfterSaleItemMapper;
import com.mall.api.aftersale.dto.AfterSaleItemDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AfterSaleItemServiceImplTest {

    @Mock
    private AfsAfterSaleItemMapper afterSaleItemMapper;

    @Mock
    private AfterSaleConvert afterSaleConvert;

    @InjectMocks
    private AfterSaleItemServiceImpl afterSaleItemService;

    @Test
    void pageItems_success() {
        final AfsAfterSaleItem item = new AfsAfterSaleItem();
        item.setId(767101L);
        item.setAfterSaleId(767001L);
        final AfterSaleItemDTO itemDTO = new AfterSaleItemDTO();
        itemDTO.setId(767101L);
        itemDTO.setAfterSaleId(767001L);
        final Page<AfsAfterSaleItem> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(item));
        when(afterSaleItemMapper.selectPage(any(), any())).thenReturn(page);
        when(afterSaleConvert.toAfterSaleItemDTO(item)).thenReturn(itemDTO);

        final PageResult<AfterSaleItemDTO> result = afterSaleItemService.pageItems(null);

        assertEquals(1L, result.getTotal());
        assertEquals(767001L, result.getList().get(0).getAfterSaleId());
    }

    @Test
    void getItem_notFound_throwNotFound() {
        when(afterSaleItemMapper.selectById(767999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> afterSaleItemService.getItem(767999L));
    }
}
