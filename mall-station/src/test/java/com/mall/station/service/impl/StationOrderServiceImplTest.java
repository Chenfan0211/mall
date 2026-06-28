package com.mall.station.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.station.dto.StationWorkbenchDTO;
import com.mall.api.station.vo.StationArrivalConfirmVO;
import com.mall.api.station.vo.StationPickupConfirmVO;
import com.mall.api.station.vo.StationWorkbenchQueryVO;
import com.mall.api.trade.dto.OrderDTO;
import com.mall.api.trade.dto.OrderItemDTO;
import com.mall.api.wms.dto.DeliveryStationDTO;
import com.mall.common.exception.BusinessException;
import com.mall.common.page.PageResult;
import com.mall.station.convert.StationConvert;
import com.mall.station.entity.FinAccount;
import com.mall.station.entity.FinCommissionDetail;
import com.mall.station.entity.OrdOrder;
import com.mall.station.entity.OrdOrderItem;
import com.mall.station.entity.OrdOrderStatusLog;
import com.mall.station.entity.WmsDeliveryOrder;
import com.mall.station.entity.WmsDeliveryStation;
import com.mall.station.mapper.FinAccountMapper;
import com.mall.station.mapper.FinCommissionDetailMapper;
import com.mall.station.mapper.MsgRecordMapper;
import com.mall.station.mapper.OpExceptionRecordMapper;
import com.mall.station.mapper.OrdOrderItemMapper;
import com.mall.station.mapper.OrdOrderMapper;
import com.mall.station.mapper.OrdOrderStatusLogMapper;
import com.mall.station.mapper.UsrStationLeaderMapper;
import com.mall.station.mapper.WmsDeliveryOrderMapper;
import com.mall.station.mapper.WmsDeliveryStationMapper;
import com.mall.station.mapper.WmsReturnHandoverMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StationOrderServiceImplTest {

    @Mock
    private OrdOrderMapper orderMapper;

    @Mock
    private OrdOrderItemMapper orderItemMapper;

    @Mock
    private WmsDeliveryOrderMapper deliveryOrderMapper;

    @Mock
    private WmsDeliveryStationMapper deliveryStationMapper;

    @Mock
    private WmsReturnHandoverMapper returnHandoverMapper;

    @Mock
    private OpExceptionRecordMapper exceptionRecordMapper;

    @Mock
    private FinAccountMapper accountMapper;

    @Mock
    private FinCommissionDetailMapper commissionDetailMapper;

    @Mock
    private MsgRecordMapper msgRecordMapper;

    @Mock
    private UsrStationLeaderMapper stationLeaderMapper;

    @Mock
    private OrdOrderStatusLogMapper orderStatusLogMapper;

    @Mock
    private StationConvert stationConvert;

    @InjectMocks
    private StationOrderServiceImpl stationOrderService;

    @Test
    void pageOrders_success() {
        final OrdOrder order = new OrdOrder();
        order.setId(761005L);
        order.setOrderNo("ORD_TEST_WAIT_PICKUP");
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(761005L);
        orderDTO.setOrderNo("ORD_TEST_WAIT_PICKUP");
        final Page<OrdOrder> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(order));
        when(orderMapper.selectPage(any(), any())).thenReturn(page);
        when(stationConvert.toOrderDTO(order)).thenReturn(orderDTO);

        final PageResult<OrderDTO> result = stationOrderService.pageOrders(null);

        assertEquals(1L, result.getTotal());
        assertEquals("ORD_TEST_WAIT_PICKUP", result.getList().get(0).getOrderNo());
    }

    @Test
    void getOrder_notFound_throwNotFound() {
        when(orderMapper.selectById(761999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> stationOrderService.getOrder(761999L));
    }

    @Test
    void getWorkbench_success() {
        final StationWorkbenchQueryVO query = new StationWorkbenchQueryVO();
        query.setStationId(720001L);
        final FinAccount account = new FinAccount();
        account.setAvailableAmount(new BigDecimal("800.0000"));
        account.setFrozenAmount(new BigDecimal("100.0000"));
        final FinCommissionDetail commission = new FinCommissionDetail();
        commission.setCommissionAmount(new BigDecimal("2.0000"));
        when(orderItemMapper.selectCount(any())).thenReturn(5L, 3L, 2L);
        when(deliveryOrderMapper.selectList(any())).thenReturn(List.of());
        when(exceptionRecordMapper.selectCount(any())).thenReturn(1L);
        when(msgRecordMapper.selectCount(any())).thenReturn(4L);
        when(accountMapper.selectList(any())).thenReturn(List.of(account));
        when(commissionDetailMapper.selectList(any())).thenReturn(List.of(commission));

        final StationWorkbenchDTO result = stationOrderService.getWorkbench(query);

        assertEquals(5L, result.getTodayOrderCount());
        assertEquals(new BigDecimal("800.0000"), result.getAvailableAmount());
        assertEquals(new BigDecimal("2.0000"), result.getPendingCommissionAmount());
    }

    @Test
    void confirmPickup_waitPickup_success() {
        final OrdOrderItem item = new OrdOrderItem();
        item.setId(762005L);
        item.setOrderId(761005L);
        item.setQty(1L);
        item.setPickedQty(0L);
        item.setRefundedQty(0L);
        item.setFulfillStatus(60L);
        final OrdOrder order = new OrdOrder();
        order.setId(761005L);
        order.setFulfillStatus(60L);
        final OrderItemDTO dto = new OrderItemDTO();
        dto.setId(762005L);
        dto.setFulfillStatus(70L);
        when(orderItemMapper.selectById(762005L)).thenReturn(item);
        when(orderItemMapper.updateById(item)).thenReturn(1);
        when(orderItemMapper.selectList(any())).thenReturn(List.of(item));
        when(orderMapper.selectById(761005L)).thenReturn(order);
        when(orderMapper.updateById(order)).thenReturn(1);
        when(orderStatusLogMapper.insert(any(OrdOrderStatusLog.class))).thenReturn(1);
        when(stationConvert.toOrderItemDTO(item)).thenReturn(dto);

        final OrderItemDTO result = stationOrderService.confirmPickup(762005L, new StationPickupConfirmVO());

        assertEquals(70L, result.getFulfillStatus());
        assertEquals(70L, item.getFulfillStatus());
        assertEquals(50L, order.getTradeStatus());
        verify(orderStatusLogMapper, times(2)).insert(any(OrdOrderStatusLog.class));
    }

    @Test
    void confirmPickup_notWaitPickup_throwConflict() {
        final OrdOrderItem item = new OrdOrderItem();
        item.setId(762002L);
        item.setQty(1L);
        item.setPickedQty(0L);
        item.setRefundedQty(0L);
        item.setFulfillStatus(20L);
        when(orderItemMapper.selectById(762002L)).thenReturn(item);

        assertThrows(BusinessException.class, () -> stationOrderService.confirmPickup(762002L, null));
    }

    @Test
    void confirmArrival_arrived_success() {
        final WmsDeliveryStation station = new WmsDeliveryStation();
        station.setId(786102L);
        station.setDeliveryId(786002L);
        station.setStatus(20L);
        final WmsDeliveryOrder order = new WmsDeliveryOrder();
        order.setId(786002L);
        order.setStatus(20L);
        final DeliveryStationDTO dto = new DeliveryStationDTO();
        dto.setId(786102L);
        dto.setStatus(30L);
        when(deliveryStationMapper.selectById(786102L)).thenReturn(station);
        when(deliveryStationMapper.updateById(station)).thenReturn(1);
        when(deliveryStationMapper.selectList(any())).thenReturn(List.of(station));
        when(deliveryOrderMapper.selectById(786002L)).thenReturn(order);
        when(deliveryOrderMapper.updateById(order)).thenReturn(1);
        when(stationConvert.toDeliveryStationDTO(station)).thenReturn(dto);

        final DeliveryStationDTO result = stationOrderService.confirmArrival(786102L, new StationArrivalConfirmVO());

        assertEquals(30L, result.getStatus());
        assertEquals(30L, order.getStatus());
    }
}
