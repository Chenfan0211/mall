package com.mall.job.service.impl;

import com.mall.job.entity.JobExceptionRecord;
import com.mall.job.entity.SysTask;
import com.mall.job.mapper.JobExceptionRecordMapper;
import com.mall.job.mapper.SysTaskMapper;
import com.mall.job.service.SystemTaskExecutionService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemTaskExecutionServiceImpl implements SystemTaskExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemTaskExecutionServiceImpl.class);
    private static final DateTimeFormatter NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final long TASK_STATUS_WAIT = 10L;
    private static final long TASK_STATUS_RUNNING = 20L;
    private static final long TASK_STATUS_SUCCESS = 30L;
    private static final long TASK_STATUS_CLOSED = 50L;
    private static final long EXCEPTION_TYPE_SYSTEM_TASK = 90L;
    private static final long EXCEPTION_PRIORITY_P1 = 2L;
    private static final long EXCEPTION_STATUS_WAIT = 10L;
    private static final AtomicLong SEQUENCE = new AtomicLong(0L);

    private final SysTaskMapper sysTaskMapper;
    private final JobExceptionRecordMapper exceptionRecordMapper;
    private final Clock clock;

    public SystemTaskExecutionServiceImpl(
            final SysTaskMapper sysTaskMapper,
            final JobExceptionRecordMapper exceptionRecordMapper,
            final Clock clock) {
        this.sysTaskMapper = sysTaskMapper;
        this.exceptionRecordMapper = exceptionRecordMapper;
        this.clock = clock;
    }

    @Override
    public long execute(
            final String taskCode,
            final String taskName,
            final String bizType,
            final String bizNo,
            final String idempotentKey,
            final TaskAction taskAction) {
        final SysTask existedTask = sysTaskMapper.selectByIdempotentKey(idempotentKey);
        if (existedTask != null && shouldSkip(existedTask)) {
            return 0L;
        }
        if (existedTask == null) {
            sysTaskMapper.insert(createTask(taskCode, taskName, bizType, bizNo, idempotentKey));
        }
        try {
            sysTaskMapper.markRunning(idempotentKey);
            final int affectedRows = taskAction.execute();
            sysTaskMapper.markSuccess(idempotentKey);
            return affectedRows;
        } catch (RuntimeException ex) {
            final String failReason = trimMessage(ex.getMessage());
            final LocalDateTime nextRetryTime = LocalDateTime.now(clock).plusMinutes(10);
            sysTaskMapper.markFailed(idempotentKey, failReason, nextRetryTime);
            exceptionRecordMapper.insert(createException(taskCode, taskName, bizNo, failReason));
            LOG.error("[系统任务/执行] - 任务执行失败: taskCode={}, bizNo={}, errorMsg={}",
                    taskCode, bizNo, ex.getMessage(), ex);
            return 0L;
        }
    }

    private boolean shouldSkip(final SysTask task) {
        final Long status = task.getStatus();
        return Long.valueOf(TASK_STATUS_RUNNING).equals(status)
                || Long.valueOf(TASK_STATUS_SUCCESS).equals(status)
                || Long.valueOf(TASK_STATUS_CLOSED).equals(status);
    }

    private SysTask createTask(
            final String taskCode,
            final String taskName,
            final String bizType,
            final String bizNo,
            final String idempotentKey) {
        final LocalDateTime now = LocalDateTime.now(clock);
        final SysTask task = new SysTask();
        task.setId(nextId());
        task.setTaskNo("TASK_" + NO_FORMATTER.format(now) + "_" + nextSequence());
        task.setTaskCode(taskCode);
        task.setTaskName(taskName);
        task.setBizType(bizType);
        task.setBizNo(bizNo);
        task.setIdempotentKey(idempotentKey);
        task.setRetryCount(0L);
        task.setStatus(TASK_STATUS_WAIT);
        task.setCreateTime(now);
        task.setUpdateTime(now);
        task.setDeleted(0L);
        return task;
    }

    private JobExceptionRecord createException(
            final String taskCode,
            final String taskName,
            final String bizNo,
            final String failReason) {
        final LocalDateTime now = LocalDateTime.now(clock);
        final JobExceptionRecord record = new JobExceptionRecord();
        record.setId(nextId());
        record.setExceptionNo("EX_JOB_" + NO_FORMATTER.format(now) + "_" + nextSequence());
        record.setExceptionType(EXCEPTION_TYPE_SYSTEM_TASK);
        record.setPriority(EXCEPTION_PRIORITY_P1);
        record.setSourceModule("job");
        record.setSourceBizType(taskCode);
        record.setSourceBizNo(bizNo);
        record.setStatus(EXCEPTION_STATUS_WAIT);
        record.setTitle(taskName + "失败");
        record.setDescription(failReason);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        record.setDeleted(0L);
        return record;
    }

    private String trimMessage(final String message) {
        if (message == null || message.isBlank()) {
            return "系统任务执行失败";
        }
        if (message.length() <= 500) {
            return message;
        }
        return message.substring(0, 500);
    }

    private Long nextId() {
        return InstantHolder.nowMillis(clock) * 1000L + nextSequence();
    }

    private long nextSequence() {
        return SEQUENCE.updateAndGet(value -> value >= 999L ? 1L : value + 1L);
    }

    private static final class InstantHolder {

        private InstantHolder() {
        }

        private static long nowMillis(final Clock clock) {
            return clock.instant().toEpochMilli();
        }
    }
}
