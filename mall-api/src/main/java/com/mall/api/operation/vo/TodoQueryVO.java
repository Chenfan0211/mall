package com.mall.api.operation.vo;

import com.mall.common.page.PageQuery;

public class TodoQueryVO extends PageQuery {

    private Long todoType;
    private Long priority;
    private Long status;
    private String sourceBizType;
    private Long processAccountId;
    private Long cityId;
    private Long supplierId;
    private Long warehouseId;

    public Long getTodoType() {
        return todoType;
    }

    public void setTodoType(final Long todoType) {
        this.todoType = todoType;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(final Long priority) {
        this.priority = priority;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public String getSourceBizType() {
        return sourceBizType;
    }

    public void setSourceBizType(final String sourceBizType) {
        this.sourceBizType = sourceBizType;
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
}
