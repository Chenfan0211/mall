<template>
  <view class="shop-page review-page product-review-page" data-m-page="reviews">
    <view class="review-hero">
      <view class="review-nav">
        <button @click="goBack">‹</button>
        <b>买家评价（{{ reviews.length }}）</b>
        <span>••• ◎</span>
      </view>
      <view class="review-stats">
        <view class="review-rate-main">
          <strong>{{ goodRate }}<small>%</small></strong>
          <span>好评率</span>
        </view>
        <view class="review-metric">
          <b>{{ reviews.length }}</b>
          <span>评价数</span>
        </view>
        <view class="review-metric">
          <b>{{ imageReviewCount }}</b>
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
      <view class="review-tag-grid">
        <button v-for="item in tags" :key="item" :class="{ active: activeTag === item }" @click="selectTag(item)">
          <span>{{ item }}</span>
          <em>{{ tagCount(item) }}</em>
        </button>
      </view>
    </view>

    <view class="review-list-page">
      <view class="review-list-count">当前筛选：{{ activeFilterLabel }} · {{ visibleReviews.length }} 条评价示例</view>

      <view v-for="item in visibleReviews" :key="item.id" class="detail-review-card">
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
        v-if="visibleReviews.length === 0"
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
import { pageComments, type UserCommentDTO } from '@/api/user';
import { navigateUser, showUserToast, useUserState } from '@/stores/userState';
import { fallbackProductImages } from '@/utils/userFallbackData';

type ReviewFilter = 'all' | 'latest' | 'image' | 'good' | 'low';

const filters: Array<{ key: ReviewFilter; label: string }> = [
    { key: 'all', label: '全部' },
    { key: 'latest', label: '最新' },
    { key: 'image', label: '图片' },
    { key: 'good', label: '5 分' },
    { key: 'low', label: '低评分' }
];
const tags = ['新鲜/色泽好', '包装完整', '提货方便', '分量足'];
const productId = ref<number | undefined>();
const score = ref<number | undefined>();
const activeFilter = ref<ReviewFilter>('all');
const activeTag = ref('');
const reviews = ref<UserCommentDTO[]>([]);
const state = useUserState();

const visibleReviews = computed(() => {
    let rows = reviews.value.filter((item) => {
        if (score.value === 2) {
            return item.score <= 2;
        }
        return !score.value || item.score === score.value;
    });
    if (activeFilter.value === 'image') {
        rows = rows.filter((item) => reviewImages(item).length > 0);
    }
    if (activeFilter.value === 'latest') {
        rows = [...rows].sort((left, right) => String(right.createTime || '').localeCompare(String(left.createTime || '')));
    }
    if (!activeTag.value) {
        return rows;
    }
    return rows.filter((item) => item.content.includes(activeTag.value.replace(/\/.*$/, '')) || item.score >= 5);
});
const goodRate = computed(() => {
    if (!reviews.value.length) {
        return 0;
    }
    const good = reviews.value.filter((item) => item.score >= 4).length;
    return Math.round((good / reviews.value.length) * 100);
});
const imageReviewCount = computed(() => reviews.value.filter((item) => reviewImages(item).length > 0).length);
const activeFilterLabel = computed(() => {
    const matched = filters.find((item) => item.key === activeFilter.value);
    const base = matched?.label || '全部';
    return activeTag.value ? `${base} / ${activeTag.value}` : base;
});

async function loadReviews() {
    try {
        const page = await pageComments(
            {
                pageNum: 1,
                pageSize: 20,
                productId: productId.value,
                score: score.value,
                stationId: state.station.id
            },
            { silent: true }
        );
        reviews.value = page.list?.length ? page.list : fallbackReviews();
    } catch {
        reviews.value = fallbackReviews();
        console.info('用户端评价接口不可用，已展示本地评价状态');
    }
}

function fallbackReviews(): UserCommentDTO[] {
    const matched = state.localReviews.filter((item) => !productId.value || item.productId === productId.value);
    const source = matched.length
        ? matched
        : [
            {
                id: 779001,
                orderNo: 'LOCAL-DEMO-1',
                productId: productId.value || 610001,
                productName: '鲜选商品',
                skuName: '默认规格',
                score: 5,
                content: '新鲜度不错，包装完整，提货方便。',
                images: [fallbackProductImages[0]],
                status: 1,
                createTime: '2026-06-27 18:20:00'
            },
            {
                id: 779002,
                orderNo: 'LOCAL-DEMO-2',
                productId: productId.value || 610001,
                productName: '鲜选商品',
                skuName: '家庭装',
                score: 5,
                content: '分量足，适合家庭分享，性价比高。',
                images: [],
                status: 1,
                createTime: '2026-06-27 16:45:00'
            },
            {
                id: 779003,
                orderNo: 'LOCAL-DEMO-3',
                productId: productId.value || 610001,
                productName: '鲜选商品',
                skuName: '默认规格',
                score: 4,
                content: '提货点离家近，整体体验稳定。',
                images: [],
                status: 1,
                createTime: '2026-06-26 20:10:00'
            }
        ];
    return source.map((item) => ({
        id: item.id,
        commentNo: `LOCAL${item.id}`,
        userId: state.user.id,
        productId: item.productId,
        skuId: 0,
        stationId: state.station.id,
        score: item.score,
        content: item.content,
        imageJson: JSON.stringify(item.images || []),
        status: item.status,
        createTime: item.createTime
    }));
}

function selectFilter(value: ReviewFilter) {
    activeFilter.value = value;
    if (value === 'good') {
        score.value = 5;
    } else if (value === 'low') {
        score.value = 2;
    } else {
        score.value = undefined;
    }
    void loadReviews();
}

function selectTag(value: string) {
    activeTag.value = activeTag.value === value ? '' : value;
}

function tagCount(tag: string) {
    return Math.max(1, reviews.value.filter((item) => item.content.includes(tag.replace(/\/.*$/, '')) || item.score >= 5).length);
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
  width: 60rpx;
  min-width: 60rpx;
  height: 60rpx;
  min-height: 60rpx;
  padding: 0;
  color: #d82236 !important;
  background: rgba(255, 255, 255, 0.86) !important;
  border: 1rpx solid rgba(216, 34, 54, 0.12);
  border-radius: 50%;
  box-shadow: 0 12rpx 28rpx rgba(216, 34, 54, 0.12);
  font-size: 46rpx;
}

.review-nav b {
  overflow: hidden;
  color: #172033;
  font-size: 34rpx;
  font-weight: 900;
  text-align: center;
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
  gap: 8rpx;
  padding: 6rpx;
  background: #f2f4f7;
  border-radius: 20rpx;
}

.review-tag-grid {
  flex-wrap: wrap;
  gap: 10rpx;
}

.review-tabs button,
.review-tag-grid button {
  flex: 0 0 auto;
  min-height: 60rpx;
  padding: 0 20rpx;
  color: #8c6a58 !important;
  background: #fffaf6 !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.review-tabs button {
  flex: 1;
  min-width: 0;
  background: transparent !important;
  border: 0;
  border-radius: 16rpx;
}

.review-tabs button.active,
.review-tag-grid button.active {
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
