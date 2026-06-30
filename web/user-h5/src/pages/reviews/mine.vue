<template>
  <view class="service-page shop-page my-reviews-page" data-m-page="myReviews">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>我的评论</b>
        <span>{{ state.localReviews.length }}条</span>
      </view>
    </view>

    <view class="service-list">
      <view class="service-filter-strip">
        <text v-for="item in filters" :key="item.label" class="pill" :class="{ active: activeFilter === item.value }" @click="activeFilter = item.value">
          {{ item.label }}
        </text>
      </view>

      <view v-for="item in filteredReviews" :key="item.id" class="service-product-row review-service-row" @click="openProduct(item.productId)">
        <view class="service-product-thumb review-score-thumb" :style="reviewThumbStyle(item.productId)">
          <span>{{ item.score }}分</span>
        </view>
        <view class="service-product-main">
          <b>{{ item.productName }}</b>
          <span>{{ item.skuName }}<br />{{ item.createTime }}</span>
          <p>{{ item.content }}</p>
          <em>{{ item.score >= 5 ? '好评' : '低评分跟进' }}</em>
        </view>
        <button class="service-action danger" @click.stop="deleteReview(item.id)">删除</button>
      </view>

      <EmptyActionCard
        v-if="filteredReviews.length === 0"
        title="暂无评论信息"
        sub="完成订单评价后会在这里展示。"
        icon="评"
        button-text="去首页逛逛"
        @action="goHome"
      />
    </view>
    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { navigateUser, showUserToast, useUserState } from '@/stores/userState';
import { findFallbackProduct } from '@/utils/userFallbackData';

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

function deleteReview(id: number) {
    const index = state.localReviews.findIndex((item) => item.id === id);
    if (index >= 0) {
        state.localReviews.splice(index, 1);
        showUserToast('已删除本地评价记录');
    }
}

function reviewThumbStyle(productId: number) {
    return {
        backgroundImage: `url("${findFallbackProduct(productId).mainImageUrl}")`
    };
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

function goMine() {
    navigateUser('/pages/mine/index', true);
}
</script>

<style lang="scss" scoped>
.review-service-row {
  align-items: start;
}

.review-score-thumb {
  position: relative;
  color: #c2412d;
  font-size: 28rpx;
}

.review-score-thumb span {
  position: absolute;
  right: 8rpx;
  bottom: 8rpx;
  padding: 4rpx 10rpx;
  color: #ffffff;
  background: rgba(23, 32, 51, 0.64);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
}
</style>
