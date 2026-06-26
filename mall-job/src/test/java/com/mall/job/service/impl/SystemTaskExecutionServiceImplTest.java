package com.mall.job.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mall.job.entity.JobExceptionRecord;
import com.mall.job.entity.SysTask;
import com.mall.job.mapper.JobExceptionRecordMapper;
import com.mall.job.mapper.SysTaskMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemTaskExecutionServiceImplTest {

    @Mock
    private SysTaskMapper sysTaskMapper;

    @Mock
    private JobExceptionRecordMapper exceptionRecordMapper;

    private Clock clock;

    private SystemTaskExecutionServiceImpl systemTaskExecutionService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-06-26T02:00:00Z"), ZoneId.of("Asia/Shanghai"));
        systemTaskExecutionService = new SystemTaskExecutionServiceImpl(sysTaskMapper, exceptionRecordMapper, clock);
    }

    @Test
    void execute_idempotentSuccess_skipBusiness() {
        final SysTask finishedTask = new SysTask();
        finishedTask.setStatus(30L);
        when(sysTaskMapper.selectByIdempotentKey("idem-order-close")).thenReturn(finishedTask);
        final AtomicInteger businessCalls = new AtomicInteger(0);

        final long affected = systemTaskExecutionService.execute(
                "ORDER_CLOSE_TIMEOUT", "未支付订单超时关闭", "order", "ALL", "idem-order-close",
                businessCalls::incrementAndGet);

        assertEquals(0L, affected);
        assertEquals(0, businessCalls.get());
        verify(sysTaskMapper, never()).insert(any(SysTask.class));
    }

    @Test
    void execute_success_markTaskSuccess() {
        when(sysTaskMapper.selectByIdempotentKey("idem-pay-remind")).thenReturn(null);

        final long affected = systemTaskExecutionService.execute(
                "PAY_REMIND_LAST_FIVE_MINUTES", "支付最后5分钟提醒", "message", "ALL", "idem-pay-remind",
                () -> 3);

        assertEquals(3L, affected);
        verify(sysTaskMapper).insert(any(SysTask.class));
        verify(sysTaskMapper).markSuccess(eq("idem-pay-remind"));
    }

    @Test
    void execute_existingTaskWithoutStatus_runsBusiness() {
        final SysTask existingTask = new SysTask();
        existingTask.setStatus(null);
        when(sysTaskMapper.selectByIdempotentKey("idem-existing-null-status")).thenReturn(existingTask);

        final long affected = systemTaskExecutionService.execute(
                "TODO_SLA_OVERDUE", "待办SLA超时升级", "todo", "ALL", "idem-existing-null-status",
                () -> 1);

        assertEquals(1L, affected);
        verify(sysTaskMapper, never()).insert(any(SysTask.class));
        verify(sysTaskMapper).markRunning(eq("idem-existing-null-status"));
        verify(sysTaskMapper).markSuccess(eq("idem-existing-null-status"));
    }

    @Test
    void execute_failure_markFailedAndCreateException() {
        when(sysTaskMapper.selectByIdempotentKey("idem-refund-retry")).thenReturn(null);

        final long affected = systemTaskExecutionService.execute(
                "REFUND_RETRY", "退款失败重试", "refund", "ALL", "idem-refund-retry",
                () -> {
                    throw new IllegalStateException("channel timeout");
                });

        assertEquals(0L, affected);
        verify(sysTaskMapper).markFailed(eq("idem-refund-retry"), eq("channel timeout"), any());
        final ArgumentCaptor<JobExceptionRecord> captor = ArgumentCaptor.forClass(JobExceptionRecord.class);
        verify(exceptionRecordMapper).insert(captor.capture());
        assertEquals("job", captor.getValue().getSourceModule());
        assertEquals("REFUND_RETRY", captor.getValue().getSourceBizType());
    }
}
