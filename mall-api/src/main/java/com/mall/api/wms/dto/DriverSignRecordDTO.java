package com.mall.api.wms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DriverSignRecordDTO {

    private Long id;
    private String signNo;
    private Long deliveryId;
    private Long stationId;
    private Long driverAccountId;
    private Long signType;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
