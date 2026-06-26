package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class DriverDTO {

    private Long id;
    private String driverNo;
    private Long accountId;
    private String driverName;
    private String driverMobile;
    private String vehicleNo;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDriverNo() {
        return driverNo;
    }

    public void setDriverNo(final String driverNo) {
        this.driverNo = driverNo;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(final String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(final String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(final String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
