package com.mall.api.user.dto;

import java.time.LocalDateTime;

public class UserCommentDTO {

    private Long id;
    private String commentNo;
    private Long userId;
    private Long productId;
    private Long skuId;
    private Long stationId;
    private Long score;
    private String content;
    private String imageJson;
    private Long responsibilityType;
    private Long status;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCommentNo() {
        return commentNo;
    }

    public void setCommentNo(final String commentNo) {
        this.commentNo = commentNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(final Long skuId) {
        this.skuId = skuId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(final Long score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getImageJson() {
        return imageJson;
    }

    public void setImageJson(final String imageJson) {
        this.imageJson = imageJson;
    }

    public Long getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(final Long responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
