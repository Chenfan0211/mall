package com.mall.operation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.time.LocalDateTime;

@TableName("op_todo")
public class OpTodo extends BaseEntity {

    private String todoNo;
    private Long todoType;
    private String sourceBizType;
    private String sourceBizNo;
    private String title;
    private Long priority;
    private String processRoleCode;
    private Long processAccountId;
    private Long cityId;
    private Long supplierId;
    private Long warehouseId;
    private Long status;
    private LocalDateTime dueTime;

    public String getTodoNo() {
        return todoNo;
    }

    public void setTodoNo(final String todoNo) {
        this.todoNo = todoNo;
    }

    public Long getTodoType() {
        return todoType;
    }

    public void setTodoType(final Long todoType) {
        this.todoType = todoType;
    }

    public String getSourceBizType() {
        return sourceBizType;
    }

    public void setSourceBizType(final String sourceBizType) {
        this.sourceBizType = sourceBizType;
    }

    public String getSourceBizNo() {
        return sourceBizNo;
    }

    public void setSourceBizNo(final String sourceBizNo) {
        this.sourceBizNo = sourceBizNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(final Long priority) {
        this.priority = priority;
    }

    public String getProcessRoleCode() {
        return processRoleCode;
    }

    public void setProcessRoleCode(final String processRoleCode) {
        this.processRoleCode = processRoleCode;
    }

    public Long getProcessAccountId() {
        return processAccountId;
    }

    public void setProcessAccountId(final Long processAccountId) {
        this.processAccountId = processAccountId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(final LocalDateTime dueTime) {
        this.dueTime = dueTime;
    }
}
