package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("usr_comment")
public class UsrComment extends BaseEntity {

    private String commentNo;
    private Long userId;
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Long skuId;
    private Long stationId;
    private Long score;
    private String content;

    @TableField("image_json")
    private String imageJson;

    private Long responsibilityType;
    private Long status;
    private Long cityId;

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(final Long orderItemId) {
        this.orderItemId = orderItemId;
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

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }
}
