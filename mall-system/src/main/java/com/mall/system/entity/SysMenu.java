package com.mall.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private Long parentId;
    private String portalCode;
    private String menuCode;
    private String menuName;
    private String routePath;
    private Long menuType;
    private Long sortNo;
    private Long visibleStatus;
    private Long status;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }

    public String getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(final String portalCode) {
        this.portalCode = portalCode;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(final String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(final String menuName) {
        this.menuName = menuName;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(final String routePath) {
        this.routePath = routePath;
    }

    public Long getMenuType() {
        return menuType;
    }

    public void setMenuType(final Long menuType) {
        this.menuType = menuType;
    }

    public Long getSortNo() {
        return sortNo;
    }

    public void setSortNo(final Long sortNo) {
        this.sortNo = sortNo;
    }

    public Long getVisibleStatus() {
        return visibleStatus;
    }

    public void setVisibleStatus(final Long visibleStatus) {
        this.visibleStatus = visibleStatus;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
