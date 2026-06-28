package com.mall.common.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoginTokenDTO implements Serializable {

    private Long accountId;
    private String username;
    private String portalCode;
    private List<String> authorities = new ArrayList<>();
    private boolean allPlatform;
    private List<Long> cityIds = new ArrayList<>();
    private List<Long> supplierIds = new ArrayList<>();
    private List<Long> stationIds = new ArrayList<>();
    private List<Long> warehouseIds = new ArrayList<>();
    private List<Long> driverIds = new ArrayList<>();
    private List<Long> leaderIds = new ArrayList<>();

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(final String portalCode) {
        this.portalCode = portalCode;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(final List<String> authorities) {
        this.authorities = authorities == null ? new ArrayList<>() : authorities;
    }

    public boolean isAllPlatform() {
        return allPlatform;
    }

    public void setAllPlatform(final boolean allPlatform) {
        this.allPlatform = allPlatform;
    }

    public List<Long> getCityIds() {
        return cityIds;
    }

    public void setCityIds(final List<Long> cityIds) {
        this.cityIds = cityIds == null ? new ArrayList<>() : cityIds;
    }

    public List<Long> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(final List<Long> supplierIds) {
        this.supplierIds = supplierIds == null ? new ArrayList<>() : supplierIds;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public void setStationIds(final List<Long> stationIds) {
        this.stationIds = stationIds == null ? new ArrayList<>() : stationIds;
    }

    public List<Long> getWarehouseIds() {
        return warehouseIds;
    }

    public void setWarehouseIds(final List<Long> warehouseIds) {
        this.warehouseIds = warehouseIds == null ? new ArrayList<>() : warehouseIds;
    }

    public List<Long> getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(final List<Long> driverIds) {
        this.driverIds = driverIds == null ? new ArrayList<>() : driverIds;
    }

    public List<Long> getLeaderIds() {
        return leaderIds;
    }

    public void setLeaderIds(final List<Long> leaderIds) {
        this.leaderIds = leaderIds == null ? new ArrayList<>() : leaderIds;
    }
}
