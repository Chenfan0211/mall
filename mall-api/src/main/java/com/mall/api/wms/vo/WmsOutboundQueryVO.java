package com.mall.api.wms.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class WmsOutboundQueryVO extends PageQuery {

    private String keyword;
    private Long warehouseId;
    private Long lineId;
    private Long stationId;
    private Long driverAccountId;
    private Long deliveryId;
    private Long loadingId;
    private Long outboundId;
    private Long waveId;
    private Long pickId;
    private Long skuId;
    private Long status;
    private LocalDate deliveryDate;
    private String bizType;
    private String bizNo;
    private Long resultStatus;
    private Long signType;
    private Long reasonType;
    private Long accountId;
    private String moduleCode;
    private String actionCode;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(final Long lineId) {
        this.lineId = lineId;
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

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(final Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getLoadingId() {
        return loadingId;
    }

    public void setLoadingId(final Long loadingId) {
        this.loadingId = loadingId;
    }

    public Long getOutboundId() {
        return outboundId;
    }

    public void setOutboundId(final Long outboundId) {
        this.outboundId = outboundId;
    }

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(final Long waveId) {
        this.waveId = waveId;
    }

    public Long getPickId() {
        return pickId;
    }

    public void setPickId(final Long pickId) {
        this.pickId = pickId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(final String bizType) {
        this.bizType = bizType;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(final String bizNo) {
        this.bizNo = bizNo;
    }

    public Long getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(final Long resultStatus) {
        this.resultStatus = resultStatus;
    }

    public Long getSignType() {
        return signType;
    }

    public void setSignType(final Long signType) {
        this.signType = signType;
    }

    public Long getReasonType() {
        return reasonType;
    }

    public void setReasonType(final Long reasonType) {
        this.reasonType = reasonType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(final String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(final String actionCode) {
        this.actionCode = actionCode;
    }
}
