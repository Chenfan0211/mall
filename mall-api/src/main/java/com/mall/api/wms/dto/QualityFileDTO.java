package com.mall.api.wms.dto;

import java.time.LocalDateTime;

public class QualityFileDTO {

    private Long id;
    private String qualityNo;
    private Long inboundId;
    private Long skuId;
    private Long qualityResult;
    private Long fileAssetId;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getQualityNo() {
        return qualityNo;
    }

    public void setQualityNo(final String qualityNo) {
        this.qualityNo = qualityNo;
    }

    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(final Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getQualityResult() {
        return qualityResult;
    }

    public void setQualityResult(final Long qualityResult) {
        this.qualityResult = qualityResult;
    }

    public Long getFileAssetId() {
        return fileAssetId;
    }

    public void setFileAssetId(final Long fileAssetId) {
        this.fileAssetId = fileAssetId;
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
