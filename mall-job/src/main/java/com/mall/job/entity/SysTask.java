package com.mall.job.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("sys_task")
public class SysTask extends BaseEntity {

    private String taskNo;
    private String taskCode;
    private String taskName;
    private String bizType;
    private String bizNo;
    private String idempotentKey;
    private Long retryCount;
    private LocalDateTime nextRetryTime;
    private Long status;
    private String failReason;

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(final String taskNo) {
        this.taskNo = taskNo;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(final String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(final String taskName) {
        this.taskName = taskName;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(final String bizType) {
        this.bizType = bizType;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(final String bizNo) {
        this.bizNo = bizNo;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(final String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }

    public Long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(final Long retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getNextRetryTime() {
        return nextRetryTime;
    }

    public void setNextRetryTime(final LocalDateTime nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(final String failReason) {
        this.failReason = failReason;
    }
}
