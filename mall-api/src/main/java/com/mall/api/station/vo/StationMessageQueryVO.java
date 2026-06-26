package com.mall.api.station.vo;

import com.mall.common.page.PageQuery;

public class StationMessageQueryVO extends PageQuery {

    private String keyword;
    private Long receiverType;
    private Long receiverId;
    private Long channelType;
    private Long readStatus;
    private Long sendStatus;
    private String bizType;
    private String bizNo;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
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
}
