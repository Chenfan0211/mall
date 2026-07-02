<template>
  <view class="shop-page review-page product-review-page" data-m-page="reviews">
    <view class="review-hero">
      <view class="review-nav">
        <button @click="goBack">‹</button>
        <b>买家评价（{{ reviewStats.commentCount || 0 }}）</b>
        <span>••• ◎</span>
      </view>
      <view class="review-stats">
        <view class="review-rate-main">
          <strong>{{ goodRate }}<small>%</small></strong>
          <span>好评率</span>
        </view>
        <view class="review-metric">
          <b>{{ reviewStats.commentCount || 0 }}</b>
          <span>评价数</span>
        </view>
        <view class="review-metric">
          <b>{{ reviewStats.imageReviewCount || 0 }}</b>
          <span>有图评价</span>
        </view>
      </view>
    </view>

    <view class="review-filter-panel">
      <view class="review-tabs">
        <button v-for="item in filters" :key="item.key" :class="{ active: activeFilter === item.key }" @click="selectFilter(item.key)">
          {{ item.label }}
        </button>
      </view>
      <view v-if="reviewTags.length" class="review-tag-grid">
        <button v-for="item in reviewTags" :key="item.label">
          <span>{{ item.label }}</span>
          <em>{{ item.count }}</em>
        </button>
      </view>
    </view>

    <view class="review-list-page">
      <view class="review-list-count">当前筛选：{{ activeFilterLabel }} · {{ reviews.length }} 条评价</view>

      <view v-for="item in reviews" :key="item.id" class="detail-review-card">
        <view class="detail-review-head">
          <span class="detail-review-user">评价记录 <span class="detail-stars">{{ stars(item.score) }}</span></span>
          <span>{{ item.createTime || '刚刚' }}</span>
        </view>
        <p>{{ item.content }}</p>
        <view v-if="reviewImages(item).length" class="detail-review-images">
          <button
            v-for="image in reviewImages(item)"
            :key="image"
            class="detail-review-img"
            :style="backgroundImageStyle(image)"
            aria-label="查看评价图片"
            @click="previewImage(image)"
          />
        </view>
        <view class="detail-review-head review-foot">
          <span>用户 {{ item.userId }}</span>
          <span>{{ item.score >= 5 ? '非常满意' : item.score >= 4 ? '满意' : '需要跟进' }}</span>
        </view>
      </view>

      <EmptyActionCard
        v-if="reviews.length === 0"
        title="暂无符合条件的评价"
        sub="切换筛选条件查看其他评价。"
        icon="评"
        button-text="返回商品"
        @action="goBack"
      />
    </view>

    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserToast from '@/components/UserToast.vue';
import { getProductReviewStats, pageComments, type UserCommentDTO, type UserProductReviewStatsDTO } from '@/api/user';
import { navigateUser, showUserToast, useUserState } from '@/stores/userState';
import { fallbackProductImages } from '@/utils/userFallbackData';

type ReviewFilter = 'all' | 'latest' | 'image';

const filters: Array<{ key: ReviewFilter; label: string }> = [
    { key: 'all', label: '全部' },
    { key: 'latest', label: '最新' },
    { key: 'image', label: '图片' }
];
const productId = ref<number | undefined>();
const activeFilter = ref<ReviewFilter>('all');
const reviews = ref<UserCommentDTO[]>([]);
const reviewStats = ref<UserProductReviewStatsDTO>(zeroReviewStats());
const state = useUserState();

const reviewTags = computed(() => (reviewStats.value.tags || []).filter((item) => Number(item.count || 0) > 0));
const goodRate = computed(() => formatGoodRate(reviewStats.value.goodRatePercent));
const activeFilterLabel = computed(() => {
    const matched = filters.find((item) => item.key === activeFilter.value);
    return matched?.label || '全部';
});

function zeroReviewStats(): UserProductReviewStatsDTO {
    return {
        commentCount: 0,
        goodRatePercent: 0,
        imageReviewCount: 0,
        recentSoldQty: 0,
        recentRepurchaseUserCount: 0,
        tags: []
    };
}

function formatGoodRate(value: string | number) {
    const numberValue = Number(value || 0);
    if (!Number.isFinite(numberValue) || numberValue <= 0) {
        return 0;
    }
    return Number.isInteger(numberValue) ? numberValue : numberValue.toFixed(1);
}

async function loadReviews() {
    try {
        const page = await pageComments(
            {
                pageNum: 1,
                pageSize: 20,
                productId: productId.value,
                cityId: state.city.id,
                stationId: state.station.id,
                sortField: activeFilter.value === 'latest' ? 'latest' : undefined,
                sortOrder: activeFilter.value === 'latest' ? 'desc' : undefined,
                hasImage: activeFilter.value === 'image' ? true : undefined
            },
            { silent: true }
        );
        reviews.value = page.list || [];
    } catch {
        reviews.value = [];
        console.info('用户端评价接口不可用，已展示空评价状态');
    }
}

async function loadReviewStats() {
    if (!productId.value) {
        reviewStats.value = zeroReviewStats();
        return;
    }
    try {
        const stats = await getProductReviewStats(
            productId.value,
            {
                cityId: state.city.id,
                stationId: state.station.id
            },
            { silent: true }
        );
        reviewStats.value = {
            commentCount: Number(stats.commentCount || 0),
            goodRatePercent: stats.goodRatePercent || 0,
            imageReviewCount: Number(stats.imageReviewCount || 0),
            recentSoldQty: Number(stats.recentSoldQty || 0),
            recentRepurchaseUserCount: Number(stats.recentRepurchaseUserCount || 0),
            tags: (stats.tags || []).filter((item) => Number(item.count || 0) > 0)
        };
    } catch {
        reviewStats.value = zeroReviewStats();
        console.info('用户端评价统计接口不可用，已展示零值统计');
    }
}

function selectFilter(value: ReviewFilter) {
    activeFilter.value = value;
    void loadReviews();
}

function stars(value: number) {
    return '★★★★★'.slice(0, Math.max(1, Math.min(5, Number(value || 5))));
}

function reviewImages(item: UserCommentDTO) {
    if (!item.imageJson) {
        return [];
    }
    try {
        const parsed = JSON.parse(item.imageJson) as string[];
        return Array.isArray(parsed) ? parsed.slice(0, 3) : [];
    } catch {
        return [];
    }
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

function previewImage(image: string) {
    showUserToast('评价图片预览暂用本页图片展示');
    void image;
}

function goBack() {
    uni.navigateBack({
        fail() {
            navigateUser('/pages/home/index', true);
        }
    });
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    productId.value = Number(current.options?.productId || current.options?.id || 0) || undefined;
    void loadReviewStats();
    void loadReviews();
});
</script>

<style lang="scss" scoped>
.product-review-page {
  min-height: 100vh;
  padding: 0 20rpx 44rpx;
  background: #f7f1ea;
}

.review-hero {
  margin: 0 -20rpx;
  min-height: 380rpx;
  padding: 84rpx 28rpx 28rpx;
  color: #172033;
  background: linear-gradient(145deg, #fff7ed 0%, #ffe8e7 54%, #ffffff 100%);
  border-radius: 0 0 36rpx 36rpx;
  box-shadow: 0 16rpx 36rpx rgba(16, 24, 40, 0.07);
}

.review-nav {
  display: grid;
  grid-template-columns: 68rpx minmax(0, 1fr) 116rpx;
  align-items: center;
  gap: 16rpx;
}

.review-nav button {
  display: grid;
  width: 64rpx;
  min-width: 64rpx;
  height: 64rpx;
  min-height: 64rpx;
  padding: 0;
  place-items: center;
  color: #d82236 !important;
  background: rgba(255, 255, 255, 0.86) !important;
  border: 1rpx solid rgba(216, 34, 54, 0.12);
  border-radius: 50%;
  box-shadow: 0 12rpx 28rpx rgba(216, 34, 54, 0.12);
  font-size: 50rpx;
  font-weight: 700;
  line-height: 1;
}

.review-nav button::after {
  border: 0;
}

.review-nav b {
  overflow: hidden;
  color: #172033;
  font-size: 34rpx;
  font-weight: 900;
  text-align: left;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.review-nav span {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56rpx;
  color: #3b4251;
  background: rgba(255, 255, 255, 0.72);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
}

.review-stats {
  display: grid;
  grid-template-columns: 1.15fr repeat(2, minmax(0, 1fr));
  gap: 0;
  align-items: center;
  justify-items: center;
  margin-top: 28rpx;
  padding: 26rpx 20rpx;
  background: rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  box-shadow: inset 0 0 0 1rpx rgba(216, 34, 54, 0.06);
}

.review-rate-main,
.review-metric {
  display: grid;
  gap: 6rpx;
  min-width: 0;
  padding: 0 16rpx;
  color: #80665c;
  background: transparent;
  border-radius: 0;
  justify-items: center;
  text-align: center;
}

.review-rate-main,
.review-metric + .review-metric {
  border-left: 1rpx solid rgba(216, 34, 54, 0.1);
}

.review-rate-main strong,
.review-metric b {
  color: #d82236;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1;
}

.review-rate-main strong {
  font-size: 68rpx;
}

.review-rate-main small {
  font-size: 25rpx;
}

.review-rate-main span,
.review-metric span {
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 800;
}

.review-filter-panel {
  display: grid;
  gap: 16rpx;
  position: relative;
  z-index: 2;
  margin: 20rpx -4rpx 0;
  padding: 20rpx;
  background: #ffffff;
  border: 1rpx solid #f1dfd4;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(16, 24, 40, 0.06);
}

.review-tabs,
.review-tag-grid {
  display: flex;
  overflow-x: auto;
  white-space: nowrap;
}

.review-tabs {
  display: inline-flex;
  width: fit-content;
  max-width: 100%;
  justify-self: flex-start;
  justify-content: flex-start;
  align-items: center;
  gap: 8rpx;
  padding: 6rpx;
  background: #f2f4f7;
  border-radius: 20rpx;
}

.review-tag-grid {
  justify-content: flex-start;
  flex-wrap: wrap;
  gap: 10rpx;
  overflow-x: visible;
  white-space: normal;
}

.review-tabs button,
.review-tabs uni-button,
.review-tag-grid button {
  flex: 0 0 auto;
  width: auto;
  margin: 0 !important;
  min-height: 60rpx;
  padding: 0 20rpx;
  text-align: left;
  color: #8c6a58 !important;
  background: #fffaf6 !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.review-tabs button,
.review-tabs uni-button {
  flex: 0 0 auto;
  min-width: 88rpx;
  padding: 0 22rpx;
  background: transparent !important;
  border: 0;
  border-radius: 16rpx;
  text-align: center;
}

.review-tabs button.active,
.review-tabs uni-button.active {
  color: #d82236 !important;
  background: #ffffff !important;
  border-color: transparent;
  box-shadow: 0 6rpx 16rpx rgba(16, 24, 40, 0.08);
}

.review-tag-grid em {
  margin-left: 6rpx;
  font-style: normal;
}

.review-list-page {
  display: grid;
  gap: 16rpx;
  padding-top: 18rpx;
}

.review-list-count {
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 800;
}

.detail-review-card {
  display: grid;
  gap: 12rpx;
  padding: 22rpx;
  background: #ffffff;
  border: 1rpx solid #f1dfd4;
  border-radius: 24rpx;
  box-shadow: 0 10rpx 22rpx rgba(109, 61, 33, 0.07);
}

.detail-review-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  color: #8c6a58;
  font-size: 25rpx;
}

.detail-review-user {
  color: #172033;
  font-weight: 900;
}

.detail-stars {
  color: #f59e0b;
}

.detail-review-card p {
  margin: 0;
  color: #172033;
  font-size: 26rpx;
  line-height: 1.5;
}

.detail-review-images {
  display: flex;
  gap: 10rpx;
}

.detail-review-img {
  width: 112rpx;
  min-width: 112rpx;
  height: 112rpx;
  min-height: 112rpx;
  padding: 0;
  background: #fff1e9;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border: 0;
  border-radius: 18rpx !important;
  box-shadow: none;
}
</style>
