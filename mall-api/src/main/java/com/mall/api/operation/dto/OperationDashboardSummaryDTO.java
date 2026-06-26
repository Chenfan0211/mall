package com.mall.api.operation.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OperationDashboardSummaryDTO implements Serializable {

    private Long pendingTodoCount;
    private Long processingTodoCount;
    private Long timeoutTodoCount;
    private Long highPriorityTodoCount;
    private Long pendingExceptionCount;
    private Long processingExceptionCount;
    private Long highPriorityExceptionCount;
    private Long closedExceptionCount;
    private List<TodoDTO> urgentTodos = new ArrayList<>();
    private List<ExceptionRecordDTO> warningExceptions = new ArrayList<>();

    public Long getPendingTodoCount() {
        return pendingTodoCount;
    }

    public void setPendingTodoCount(final Long pendingTodoCount) {
        this.pendingTodoCount = pendingTodoCount;
    }

    public Long getProcessingTodoCount() {
        return processingTodoCount;
    }

    public void setProcessingTodoCount(final Long processingTodoCount) {
        this.processingTodoCount = processingTodoCount;
    }

    public Long getTimeoutTodoCount() {
        return timeoutTodoCount;
    }

    public void setTimeoutTodoCount(final Long timeoutTodoCount) {
        this.timeoutTodoCount = timeoutTodoCount;
    }

    public Long getHighPriorityTodoCount() {
        return highPriorityTodoCount;
    }

    public void setHighPriorityTodoCount(final Long highPriorityTodoCount) {
        this.highPriorityTodoCount = highPriorityTodoCount;
    }

    public Long getPendingExceptionCount() {
        return pendingExceptionCount;
    }

    public void setPendingExceptionCount(final Long pendingExceptionCount) {
        this.pendingExceptionCount = pendingExceptionCount;
    }

    public Long getProcessingExceptionCount() {
        return processingExceptionCount;
    }

    public void setProcessingExceptionCount(final Long processingExceptionCount) {
        this.processingExceptionCount = processingExceptionCount;
    }

    public Long getHighPriorityExceptionCount() {
        return highPriorityExceptionCount;
    }

    public void setHighPriorityExceptionCount(final Long highPriorityExceptionCount) {
        this.highPriorityExceptionCount = highPriorityExceptionCount;
    }

    public Long getClosedExceptionCount() {
        return closedExceptionCount;
    }

    public void setClosedExceptionCount(final Long closedExceptionCount) {
        this.closedExceptionCount = closedExceptionCount;
    }

    public List<TodoDTO> getUrgentTodos() {
        return urgentTodos;
    }

    public void setUrgentTodos(final List<TodoDTO> urgentTodos) {
        this.urgentTodos = urgentTodos == null ? new ArrayList<>() : urgentTodos;
    }

    public List<ExceptionRecordDTO> getWarningExceptions() {
        return warningExceptions;
    }

    public void setWarningExceptions(final List<ExceptionRecordDTO> warningExceptions) {
        this.warningExceptions = warningExceptions == null ? new ArrayList<>() : warningExceptions;
    }
}
