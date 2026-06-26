package com.mall.api.system.dto;

import java.io.Serializable;

public class RoleDTO implements Serializable {

    private Long id;
    private String roleCode;
    private String roleName;
    private String portalCode;
    private Long roleType;
    private Long status;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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
