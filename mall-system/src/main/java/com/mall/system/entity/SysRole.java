package com.mall.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("sys_role")
public class SysRole extends BaseEntity {

    private String roleCode;
    private String roleName;
    private String portalCode;
    private Long roleType;
    private Long status;
    private String remark;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(final String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public String getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(final String portalCode) {
        this.portalCode = portalCode;
    }

    public Long getRoleType() {
        return roleType;
    }

    public void setRoleType(final Long roleType) {
        this.roleType = roleType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }
}
