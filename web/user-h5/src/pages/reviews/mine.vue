<template>
  <view class="page no-tab">
    <view class="section">
      <text class="title">我的评价</text>
      <text class="subtle">展示已发表评价，低评分会关联运营跟进。</text>
    </view>

    <view class="section">
      <view class="chip-row">
        <text v-for="item in filters" :key="item.label" class="pill" :class="{ active: activeFilter === item.value }" @click="activeFilter = item.value">
          {{ item.label }}
        </text>
      </view>
    </view>

    <view v-for="item in filteredReviews" :key="item.id" class="section review-row" @click="openProduct(item.productId)">
      <view class="score-pill">{{ item.score }}分</view>
      <view>
        <text>{{ item.productName }}</text>
        <text>{{ item.skuName }} · {{ item.createTime }}</text>
        <text>{{ item.content }}</text>
      </view>
    </view>

    <EmptyActionCard
      v-if="filteredReviews.length === 0"
      title="暂无评价"
      sub="完成订单后可在订单详情里评价商品。"
      icon="评"
      button-text="看订单"
      @action="goOrder"
    />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import { navigateUser, useUserState } from '@/stores/userState';

const state = useUserState();
const activeFilter = ref('all');
const filters = [
    { label: '全部', value: 'all' },
    { label: '5分好评', value: 'good' },
    { label: '低评分', value: 'low' }
];
const filteredReviews = computed(() => {
    if (activeFilter.value === 'good') {
        return state.localReviews.filter((item) => item.score >= 5);
    }
    if (activeFilter.value === 'low') {
        return state.localReviews.filter((item) => item.score <= 2);
    }
    return state.localReviews;
});

function openProduct(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goOrder() {
    uni.switchTab({ url: '/pages/order/index' });
}
</script>

<style lang="scss" scoped>
.review-row {
  display: grid;
  grid-template-columns: 82rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: start;
}

.score-pill {
  display: grid;
  width: 82rpx;
  height: 82rpx;
  place-items: center;
  color: #d94b34;
  background: #fff1e9;
  border: 1rpx solid #f2d6c4;
  border-radius: 22rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.review-row view:last-child {
  min-width: 0;
}

.review-row view:last-child text {
  display: block;
}

.review-row view:last-child text:first-child {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.review-row view:last-child text:not(:first-child) {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 23rpx;
}
</style>
