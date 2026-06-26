package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_driver")
public class WmsDriver extends BaseEntity {

    private String driverNo;
    private Long accountId;
    private String driverName;
    private String driverMobile;
    private String vehicleNo;
    private Long status;

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
}
