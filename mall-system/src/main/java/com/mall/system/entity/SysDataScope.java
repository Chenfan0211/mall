package com.mall.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("sys_data_scope")
public class SysDataScope extends BaseEntity {

    private String scopeCode;
    private String scopeName;
    private Long scopeType;
    private Long scopeObjectId;
    private Long includeChildFlag;
    private Long status;

    public String getScopeCode() {
        return scopeCode;
    }

    public void setScopeCode(final String scopeCode) {
        this.scopeCode = scopeCode;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(final String scopeName) {
        this.scopeName = scopeName;
    }

    public Long getScopeType() {
        return scopeType;
    }

    public void setScopeType(final Long scopeType) {
        this.scopeType = scopeType;
    }

    public Long getScopeObjectId() {
        return scopeObjectId;
    }

    public void setScopeObjectId(final Long scopeObjectId) {
        this.scopeObjectId = scopeObjectId;
    }

    public Long getIncludeChildFlag() {
        return includeChildFlag;
    }

    public void setIncludeChildFlag(final Long includeChildFlag) {
        this.includeChildFlag = includeChildFlag;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
