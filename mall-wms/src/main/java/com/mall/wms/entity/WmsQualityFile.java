package com.mall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("wms_quality_file")
public class WmsQualityFile extends BaseEntity {

    private String qualityNo;
    private Long inboundId;
    private Long skuId;
    private Long qualityResult;
    private Long fileAssetId;
    private String remark;

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
}
