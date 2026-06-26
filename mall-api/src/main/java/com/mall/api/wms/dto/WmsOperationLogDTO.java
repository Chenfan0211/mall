package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class WmsOperationLogDTO {

    private Long id;
    private String logNo;
    private Long warehouseId;
    private Long accountId;
    private String roleCode;
    private String moduleCode;
    private String actionCode;
    private String bizType;
    private String bizNo;
    private Long beforeStatus;
    private Long afterStatus;
    private String reason;
    private Long resultStatus;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLogNo() {
        return logNo;
    }

    public void setLogNo(final String logNo) {
        this.logNo = logNo;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(final String roleCode) {
        this.roleCode = roleCode;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(final String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(final String actionCode) {
        this.actionCode = actionCode;
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

    public Long getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(final Long beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public Long getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(final Long afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public Long getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(final Long resultStatus) {
        this.resultStatus = resultStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
