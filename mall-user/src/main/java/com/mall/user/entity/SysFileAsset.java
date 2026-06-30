package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("sys_file_asset")
public class SysFileAsset extends BaseEntity {

    private String fileNo;
    private String bizType;
    private String bizNo;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private Long uploaderAccountId;
    private Long status;

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(final String fileNo) {
        this.fileNo = fileNo;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(final String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(final Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(final String fileType) {
        this.fileType = fileType;
    }

    public Long getUploaderAccountId() {
        return uploaderAccountId;
    }

    public void setUploaderAccountId(final Long uploaderAccountId) {
        this.uploaderAccountId = uploaderAccountId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
