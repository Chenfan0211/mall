package com.mall.api.user.dto;

public class UserHomePromoDTO {

    private String code;
    private String title;
    private String actionText;
    private String imageUrl;
    private String linkUrl;
    private String activityCode;
    private Long sortNo;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(final String actionText) {
        this.actionText = actionText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(final String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(final String activityCode) {
        this.activityCode = activityCode;
    }

    public Long getSortNo() {
        return sortNo;
    }

    public void setSortNo(final Long sortNo) {
        this.sortNo = sortNo;
    }
}
