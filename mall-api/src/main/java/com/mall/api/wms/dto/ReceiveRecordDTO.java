package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class ReceiveRecordDTO {

    private Long id;
    private String receiveNo;
    private Long inboundId;
    private Long inboundItemId;
    private Long skuId;
    private Long receiveQty;
    private Long rejectQty;
    private Long diffQty;
    private Long receiveAccountId;
    private Long qualityStatus;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getReceiveNo() {
        return receiveNo;
    }

    public void setReceiveNo(final String receiveNo) {
        this.receiveNo = receiveNo;
    }

    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(final Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getInboundItemId() {
        return inboundItemId;
    }

    public void setInboundItemId(final Long inboundItemId) {
        this.inboundItemId = inboundItemId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getReceiveQty() {
        return receiveQty;
    }

    public void setReceiveQty(final Long receiveQty) {
        this.receiveQty = receiveQty;
    }

    public Long getRejectQty() {
        return rejectQty;
    }

    public void setRejectQty(final Long rejectQty) {
        this.rejectQty = rejectQty;
    }

    public Long getDiffQty() {
        return diffQty;
    }

    public void setDiffQty(final Long diffQty) {
        this.diffQty = diffQty;
    }

    public Long getReceiveAccountId() {
        return receiveAccountId;
    }

    public void setReceiveAccountId(final Long receiveAccountId) {
        this.receiveAccountId = receiveAccountId;
    }

    public Long getQualityStatus() {
        return qualityStatus;
    }

    public void setQualityStatus(final Long qualityStatus) {
        this.qualityStatus = qualityStatus;
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
