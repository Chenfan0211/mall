package com.mall.job.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mall.job.mapper.MallJobBizMapper;
import com.mall.job.service.SystemTaskExecutionService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MallScheduledJobServiceImplTest {

    @Mock
    private MallJobBizMapper jobBizMapper;

    @Mock
    private SystemTaskExecutionService systemTaskExecutionService;

    private Clock clock;

    private MallScheduledJobServiceImpl mallScheduledJobService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-06-26T02:00:00Z"), ZoneId.of("Asia/Shanghai"));
        mallScheduledJobService = new MallScheduledJobServiceImpl(jobBizMapper, systemTaskExecutionService, clock);
    }

    @Test
    void closeTimeoutOrders_success() {
        when(systemTaskExecutionService.execute(eq("ORDER_CLOSE_TIMEOUT"), eq("未支付订单超时关闭"),
                eq("order"), eq("ALL"), anyString(), org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
                    final SystemTaskExecutionService.TaskAction action = invocation.getArgument(5);
                    return (long) action.execute();
                });
        when(jobBizMapper.closeTimeoutOrders()).thenReturn(2);
        when(jobBizMapper.releaseTimeoutOrderLocks()).thenReturn(2);

        final long affected = mallScheduledJobService.closeTimeoutOrders();

        assertEquals(4L, affected);
        verify(jobBizMapper).closeTimeoutOrders();
        verify(jobBizMapper).releaseTimeoutOrderLocks();
    }

    @Test
    void closeDuePublishedPeriods_success() {
        when(systemTaskExecutionService.execute(eq("SALE_PERIOD_CUTOFF"), eq("团期截团处理"),
                eq("sale_period"), eq("ALL"), anyString(), org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
                    final SystemTaskExecutionService.TaskAction action = invocation.getArgument(5);
                    return (long) action.execute();
                });
        when(jobBizMapper.generateVirtualPurchaseOrders()).thenReturn(1);
        when(jobBizMapper.generateVirtualPurchaseItems()).thenReturn(1);
        when(jobBizMapper.generateWarehouseWaves()).thenReturn(2);
        when(jobBizMapper.generatePickTasks()).thenReturn(2);
        when(jobBizMapper.generateLoadingOrders()).thenReturn(2);
        when(jobBizMapper.closeDuePublishedPeriods()).thenReturn(3);

        final long affected = mallScheduledJobService.closeDuePublishedPeriods();

        assertEquals(11L, affected);
        verify(jobBizMapper).generateVirtualPurchaseOrders();
        verify(jobBizMapper).generateVirtualPurchaseItems();
        verify(jobBizMapper).generateWarehouseWaves();
        verify(jobBizMapper).generatePickTasks();
        verify(jobBizMapper).generateLoadingOrders();
        verify(jobBizMapper).closeDuePublishedPeriods();
    }

    @Test
    void retryFailedRefunds_success() {
        when(systemTaskExecutionService.execute(eq("REFUND_RETRY"), eq("退款失败重试"),
                eq("refund"), eq("ALL"), anyString(), org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
                    final SystemTaskExecutionService.TaskAction action = invocation.getArgument(5);
                    return (long) action.execute();
                });
        when(jobBizMapper.retryFailedRefunds()).thenReturn(5);

        final long affected = mallScheduledJobService.retryFailedRefunds();

        assertEquals(5L, affected);
        verify(jobBizMapper).retryFailedRefunds();
    }

    @Test
    void detectLowInventory_success() {
        when(systemTaskExecutionService.execute(eq("LOW_INVENTORY_CHECK"), eq("低库存补货检测"),
                eq("inventory"), eq("ALL"), anyString(), org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
                    final SystemTaskExecutionService.TaskAction action = invocation.getArgument(5);
                    return (long) action.execute();
                });
        when(jobBizMapper.detectLowInventory()).thenReturn(2);

        final long affected = mallScheduledJobService.detectLowInventory();

        assertEquals(2L, affected);
        verify(jobBizMapper).detectLowInventory();
    }
}
