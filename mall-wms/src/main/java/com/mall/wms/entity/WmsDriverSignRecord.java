package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;
import java.math.BigDecimal;

@TableName("wms_driver_sign_record")
public class WmsDriverSignRecord extends BaseEntity {

    private String signNo;
    private Long deliveryId;
    private Long stationId;
    private Long driverAccountId;
    private Long signType;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String remark;

    public String getSignNo() {
        return signNo;
    }

    public void setSignNo(final String signNo) {
        this.signNo = signNo;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getDriverAccountId() {
        return driverAccountId;
    }

    public void setDriverAccountId(final Long driverAccountId) {
        this.driverAccountId = driverAccountId;
    }

    public Long getSignType() {
        return signType;
    }

    public void setSignType(final Long signType) {
        this.signType = signType;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(final BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(final BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }
}
