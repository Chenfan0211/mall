package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("ord_fulfillment_track")
public class OrdFulfillmentTrack extends BaseEntity {

    private String trackNo;
    private Long orderId;
    private Long orderItemId;
    private Long nodeType;
    private String nodeName;
    private String bizNo;
    private Long status;
    private String remark;
    private Long cityId;
    private Long stationId;
    private Long warehouseId;

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(final String trackNo) {
        this.trackNo = trackNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(final Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getNodeType() {
        return nodeType;
    }

    public void setNodeType(final Long nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(final String bizNo) {
        this.bizNo = bizNo;
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

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }
}
