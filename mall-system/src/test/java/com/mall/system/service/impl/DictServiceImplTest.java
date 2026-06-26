package com.mall.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.system.dto.DictItemDTO;
import com.mall.api.system.vo.DictItemQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysDictItem;
import com.mall.system.mapper.SysDictItemMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DictServiceImplTest {

    @Mock
    private SysDictItemMapper dictItemMapper;

    @Mock
    private SystemConvert systemConvert;

    @InjectMocks
    private DictServiceImpl dictService;

    @Test
    void listItems_success() {
        final DictItemQueryVO query = new DictItemQueryVO();
        query.setDictCode("order_status");
        final SysDictItem item = new SysDictItem();
        item.setId(801001L);
        item.setItemName("待支付");
        final DictItemDTO dictItemDTO = new DictItemDTO();
        dictItemDTO.setId(801001L);
        dictItemDTO.setItemName("待支付");
        when(dictItemMapper.selectList(any())).thenReturn(List.of(item));
        when(systemConvert.toDictItemDTOList(List.of(item))).thenReturn(List.of(dictItemDTO));

        final List<DictItemDTO> result = dictService.listItems(query);

        assertEquals(1, result.size());
        assertEquals("待支付", result.get(0).getItemName());
    }

    @Test
    void listItems_missingDictCode_throwParamError() {
        final DictItemQueryVO query = new DictItemQueryVO();

        assertThrows(BusinessException.class, () -> dictService.listItems(query));
    }
}
