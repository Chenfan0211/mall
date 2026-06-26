package com.mall.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mall.api.aftersale.dto.AfterSaleDTO;
import com.mall.api.user.vo.UserAfterSaleApplyVO;
import com.mall.common.exception.BusinessException;
import com.mall.user.convert.UserConvert;
import com.mall.user.entity.AfsAfterSale;
import com.mall.user.entity.OrdOrder;
import com.mall.user.entity.OrdOrderItem;
import com.mall.user.mapper.AfsAfterSaleItemMapper;
import com.mall.user.mapper.AfsAfterSaleMapper;
import com.mall.user.mapper.AfsReturnRecordMapper;
import com.mall.user.mapper.OrdOrderItemMapper;
import com.mall.user.mapper.OrdOrderMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAfterSaleServiceImplTest {

    @Mock
    private AfsAfterSaleMapper afterSaleMapper;

    @Mock
    private AfsAfterSaleItemMapper afterSaleItemMapper;

    @Mock
    private AfsReturnRecordMapper returnRecordMapper;

    @Mock
    private OrdOrderMapper orderMapper;

    @Mock
    private OrdOrderItemMapper orderItemMapper;

    @Mock
    private UserConvert userConvert;

    @InjectMocks
    private UserAfterSaleServiceImpl afterSaleService;

    @Test
    void applyAfterSale_success() {
        final UserAfterSaleApplyVO request = buildRequest();
        final OrdOrderItem item = buildItem();
        final OrdOrder order = buildOrder();
        final AfterSaleDTO dto = new AfterSaleDTO();
        dto.setStatus(10L);
        when(orderItemMapper.selectById(762005L)).thenReturn(item);
        when(orderMapper.selectById(761005L)).thenReturn(order);
        when(userConvert.toAfterSaleDTO(any(AfsAfterSale.class))).thenReturn(dto);

        final AfterSaleDTO result = afterSaleService.applyAfterSale(request);

        assertEquals(10L, item.getAfterSaleStatus());
        assertEquals(10L, result.getStatus());
        verify(afterSaleMapper).insert(any(AfsAfterSale.class));
        verify(orderItemMapper).updateById(item);
    }

    @Test
    void applyAfterSale_overRemainQty_throwConflict() {
        final UserAfterSaleApplyVO request = buildRequest();
        request.setApplyQty(3L);
        final OrdOrderItem item = buildItem();
        final OrdOrder order = buildOrder();
        when(orderItemMapper.selectById(762005L)).thenReturn(item);
        when(orderMapper.selectById(761005L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> afterSaleService.applyAfterSale(request));
    }

    @Test
    void applyAfterSale_missingSalePrice_throwConflict() {
        final UserAfterSaleApplyVO request = buildRequest();
        final OrdOrderItem item = buildItem();
        item.setSalePrice(null);
        final OrdOrder order = buildOrder();
        when(orderItemMapper.selectById(762005L)).thenReturn(item);
        when(orderMapper.selectById(761005L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> afterSaleService.applyAfterSale(request));
    }

    private UserAfterSaleApplyVO buildRequest() {
        final UserAfterSaleApplyVO request = new UserAfterSaleApplyVO();
        request.setUserId(740001L);
        request.setOrderItemId(762005L);
        request.setAfterSaleType(1L);
        request.setApplyQty(1L);
        request.setRefundAmount(new BigDecimal("19.90"));
        request.setApplyReason("商品有问题");
        return request;
    }

    private OrdOrderItem buildItem() {
        final OrdOrderItem item = new OrdOrderItem();
        item.setId(762005L);
        item.setOrderId(761005L);
        item.setSkuId(752001L);
        item.setSupplierId(710001L);
        item.setQty(2L);
        item.setRefundedQty(0L);
        item.setSalePrice(new BigDecimal("19.90"));
        item.setFulfillStatus(60L);
        item.setAfterSaleStatus(0L);
        return item;
    }

    private OrdOrder buildOrder() {
        final OrdOrder order = new OrdOrder();
        order.setId(761005L);
        order.setOrderNo("ORD_TEST_COMPLETED");
        order.setUserId(740001L);
        order.setCityId(440100L);
        order.setStationId(720001L);
        order.setTradeStatus(50L);
        return order;
    }
}
