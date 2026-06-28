<template>
  <view class="page no-tab review-page">
    <view class="warm-head">
      <text class="head-kicker">订单评价</text>
      <text class="head-title">{{ order?.orderNo || '选择评价商品' }}</text>
      <text class="head-sub">评价会展示在商品详情页，低评分可关联售后。</text>
    </view>

    <view v-if="order" class="section">
      <text class="title">评价商品</text>
      <view v-for="item in order.items" :key="item.id" class="review-item" :class="{ active: activeItemId === item.id }" @click="activeItemId = item.id">
        <view class="item-thumb">评</view>
        <view>
          <text>{{ item.productName }}</text>
          <text>{{ item.skuName }} · ×{{ item.qty }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="title">商品评分</text>
      <view class="star-row">
        <text v-for="item in 5" :key="item" :class="{ active: score >= item }" @click="score = item">★</text>
      </view>
      <view class="chip-row">
        <text v-for="item in tags" :key="item" class="pill" :class="{ active: selectedTags.includes(item) }" @click="toggleTag(item)">
          {{ item }}
        </text>
      </view>
    </view>

    <view class="section">
      <text class="title">评价内容</text>
      <textarea v-model="content" maxlength="200" placeholder="说说商品新鲜度、包装和提货体验" />
      <view class="image-row">
        <view v-for="item in images" :key="item" class="review-image" @click="previewImage(item)">{{ item }}</view>
        <button class="plain small" @click="addImage">上传图片</button>
      </view>
    </view>

    <button class="submit-review" :disabled="!canSubmit" @click="submitReview">提交评价</button>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { findLocalOrder, showUserToast, type LocalOrder } from '@/stores/userState';

const order = ref<LocalOrder>();
const activeItemId = ref<number>();
const score = ref(5);
const content = ref('');
const selectedTags = ref<string[]>([]);
const images = ref<string[]>([]);
const tags = ['新鲜', '包装好', '提货方便', '分量足', '有破损', '不新鲜'];
const canSubmit = computed(() => Boolean(activeItemId.value && content.value.trim().length >= 4));

function loadOrder() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    const id = Number(current.options?.id || current.options?.orderId || 0);
    order.value = findLocalOrder(id);
    activeItemId.value = order.value?.items[0]?.id;
}

function toggleTag(tag: string) {
    selectedTags.value = selectedTags.value.includes(tag)
        ? selectedTags.value.filter((item) => item !== tag)
        : [...selectedTags.value, tag];
}

function addImage() {
    if (images.value.length >= 3) {
        showUserToast('最多上传 3 张图片', 'warn');
        return;
    }
    images.value.push(`图${images.value.length + 1}`);
    showUserToast('图片上传接口暂缺，已添加演示图片');
}

function previewImage(image: string) {
    showUserToast(`预览 ${image}`);
}

function submitReview() {
    if (!canSubmit.value) {
        showUserToast('请填写至少 4 个字的评价内容', 'warn');
        return;
    }
    showUserToast(score.value <= 2 ? '评价已提交，低评分将同步给运营跟进' : '评价已提交');
    setTimeout(() => {
        uni.navigateBack({
            fail() {
                uni.switchTab({ url: '/pages/order/index' });
            }
        });
    }, 500);
}

onMounted(loadOrder);
</script>

<style lang="scss" scoped>
.head-kicker,
.head-title,
.head-sub {
  display: block;
}

.head-kicker {
  color: rgba(255, 255, 255, 0.82);
  font-size: 22rpx;
  font-weight: 900;
}

.head-title {
  margin-top: 8rpx;
  color: #ffffff;
  font-size: 40rpx;
  font-weight: 900;
}

.head-sub {
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.88);
  font-size: 24rpx;
}

.review-item {
  display: grid;
  grid-template-columns: 82rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: center;
  margin-top: 16rpx;
  padding: 16rpx;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
}

.review-item.active {
  border-color: #e85d3f;
  background: #fff1e9;
}

.item-thumb {
  display: grid;
  width: 82rpx;
  height: 82rpx;
  place-items: center;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7, #f05a37);
  border-radius: 18rpx;
  font-weight: 900;
}

.review-item text {
  display: block;
}

.review-item text:first-child {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.review-item text:last-child {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.star-row {
  display: flex;
  gap: 12rpx;
  margin: 18rpx 0;
}

.star-row text {
  color: #d9c8be;
  font-size: 54rpx;
  line-height: 1;
}

.star-row text.active {
  color: #f59e0b;
}

.image-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 16rpx;
}

.review-image {
  display: grid;
  width: 104rpx;
  height: 104rpx;
  place-items: center;
  color: #c2412d;
  background: #fff1e9;
  border: 1rpx solid #f2d6c4;
  border-radius: 18rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.submit-review {
  width: calc(100% - 40rpx);
  margin: 22rpx 20rpx 0;
}
</style>
