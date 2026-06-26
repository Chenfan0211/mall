package com.mall.job.service;

public interface SystemTaskExecutionService {

    long execute(
            String taskCode,
            String taskName,
            String bizType,
            String bizNo,
            String idempotentKey,
            TaskAction taskAction);

    @FunctionalInterface
    interface TaskAction {

        int execute();
    }
}
