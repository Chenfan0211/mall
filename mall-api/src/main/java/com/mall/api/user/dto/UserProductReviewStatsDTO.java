package com.mall.api.user.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserProductReviewStatsDTO {

    private Long commentCount = 0L;
    private BigDecimal goodRatePercent = BigDecimal.ZERO;
    private Long imageReviewCount = 0L;
    private Long recentSoldQty = 0L;
    private Long recentRepurchaseUserCount = 0L;
    private List<TagCountDTO> tags = new ArrayList<>();

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(final Long commentCount) {
        this.commentCount = commentCount;
    }

    public BigDecimal getGoodRatePercent() {
        return goodRatePercent;
    }

    public void setGoodRatePercent(final BigDecimal goodRatePercent) {
        this.goodRatePercent = goodRatePercent;
    }

    public Long getImageReviewCount() {
        return imageReviewCount;
    }

    public void setImageReviewCount(final Long imageReviewCount) {
        this.imageReviewCount = imageReviewCount;
    }

    public Long getRecentSoldQty() {
        return recentSoldQty;
    }

    public void setRecentSoldQty(final Long recentSoldQty) {
        this.recentSoldQty = recentSoldQty;
    }

    public Long getRecentRepurchaseUserCount() {
        return recentRepurchaseUserCount;
    }

    public void setRecentRepurchaseUserCount(final Long recentRepurchaseUserCount) {
        this.recentRepurchaseUserCount = recentRepurchaseUserCount;
    }

    public List<TagCountDTO> getTags() {
        return tags;
    }

    public void setTags(final List<TagCountDTO> tags) {
        this.tags = tags;
    }

    public static class TagCountDTO {

        private String label;
        private Long count = 0L;

        public TagCountDTO() {
        }

        public TagCountDTO(final String label, final Long count) {
            this.label = label;
            this.count = count;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(final String label) {
            this.label = label;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(final Long count) {
            this.count = count;
        }
    }
}
