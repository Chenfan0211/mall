package com.mall.api.user.dto;

import java.time.LocalDateTime;

public class UserMessageDTO {

    private Long id;
    private String msgNo;
    private String templateCode;
    private Long receiverType;
    private Long receiverId;
    private Long channelType;
    private String title;
    private String content;
    private String bizType;
    private String bizNo;
    private Long readStatus;
    private Long sendStatus;
    private String failReason;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(final String msgNo) {
        this.msgNo = msgNo;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(final String templateCode) {
        this.templateCode = templateCode;
    }

    public Long getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(final Long receiverType) {
        this.receiverType = receiverType;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(final Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getChannelType() {
        return channelType;
    }

    public void setChannelType(final Long channelType) {
        this.channelType = channelType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
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

    public Long getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(final Long readStatus) {
        this.readStatus = readStatus;
    }

    public Long getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(final Long sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(final String failReason) {
        this.failReason = failReason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
