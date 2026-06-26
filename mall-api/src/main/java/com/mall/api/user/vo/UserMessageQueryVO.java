package com.mall.api.user.vo;

import com.mall.common.page.PageQuery;
import jakarta.validation.constraints.NotNull;

public class UserMessageQueryVO extends PageQuery {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String keyword;
    private Long channelType;
    private Long readStatus;
    private Long sendStatus;
    private String bizType;
    private String bizNo;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
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
