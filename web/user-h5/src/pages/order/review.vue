<template>
  <view class="order-detail-page shop-page order-review-page" data-m-page="orderReview">
    <view class="order-detail-hero">
      <view class="order-hero-img" :style="backgroundImageStyle(heroImage)" />
      <view class="order-hero-mask"></view>
      <view class="order-detail-nav">
        <button class="order-detail-back" @click="goBack">‹</button>
        <b>订单评价</b>
        <span>提交评价</span>
      </view>
      <view class="order-detail-status">
        <span class="order-status">评价</span>
        <h2>评价商品</h2>
        <p>选择商品后填写评价。</p>
      </view>
    </view>

    <view class="order-detail-body">
      <view v-if="order" class="order-info-card">
        <h3>选择评价商品</h3>
        <view
          v-for="item in order.items"
          :key="item.id"
          class="review-pick-row"
          :class="{ active: activeItemId === item.id }"
          @click="activeItemId = item.id"
        >
          <view class="review-pick-thumb" :style="backgroundImageStyle(item.image)" />
          <view>
            <b>{{ item.productName }}</b>
            <span>{{ item.skuName }} · ×{{ item.qty }}</span>
          </view>
        </view>
      </view>

      <EmptyActionCard
        v-else
        title="未找到订单"
        sub="请从订单详情进入评价。"
        icon="单"
        button-text="看订单"
        @action="goOrder"
      />

      <view class="order-info-card">
        <h3>商品评分</h3>
        <view class="star-row">
          <text v-for="item in 5" :key="item" :class="{ active: score >= item }" @click="score = item">★</text>
        </view>
        <view class="chip-row">
          <text v-for="item in tags" :key="item" class="pill" :class="{ active: selectedTags.includes(item) }" @click="toggleTag(item)">
            {{ item }}
          </text>
        </view>
      </view>

      <view class="order-info-card service-form-card">
        <h3>评价内容</h3>
        <textarea v-model="content" maxlength="200" placeholder="说说商品新鲜度、包装和提货体验" />
        <view class="review-image-row">
          <view
            v-for="item in images"
            :key="item"
            class="review-image"
            :style="backgroundImageStyle(item)"
            @click="previewImage(item)"
          />
          <button class="plain small" @click="addImage">上传图片</button>
        </view>
      </view>

      <view class="detail-action-row">
        <button class="plain" @click="goBack">返回</button>
        <button class="primary" :disabled="!canSubmit" @click="submitReview">提交评价</button>
      </view>
    </view>

    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserToast from '@/components/UserToast.vue';
import { findLocalOrder, navigateUser, showUserToast, type LocalOrder } from '@/stores/userState';

const order = ref<LocalOrder>();
const activeItemId = ref<number>();
const score = ref(5);
const content = ref('');
const selectedTags = ref<string[]>([]);
const images = ref<string[]>([]);
const fallbackOrderImage = '/static/user-home/shop-detail.jpg';
const tags = ['新鲜', '包装好', '提货方便', '分量足', '有破损', '不新鲜'];
const canSubmit = computed(() => Boolean(activeItemId.value && content.value.trim().length >= 4));
const activeItem = computed(() => order.value?.items.find((item) => item.id === activeItemId.value) || order.value?.items[0]);
const heroImage = computed(() => activeItem.value?.image || fallbackOrderImage);

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

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackOrderImage}")`
    };
}

function addImage() {
    if (images.value.length >= 3) {
        showUserToast('最多上传 3 张图片', 'warn');
        return;
    }
    const samples = [
        activeItem.value?.image || fallbackOrderImage,
        '/static/user-home/shop-list.jpg',
        '/static/user-home/shop-category.jpg'
    ];
    images.value.push(samples[images.value.length] || fallbackOrderImage);
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
                navigateUser('/pages/order/index', true);
            }
        });
    }, 500);
}

function goBack() {
    uni.navigateBack({
        fail() {
            goOrder();
        }
    });
}

function goOrder() {
    navigateUser('/pages/order/index', true);
}

onMounted(loadOrder);
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: 100vh;
  padding-bottom: 236rpx;
  background: #f7f1ea;
}

.order-detail-hero {
  position: relative;
  min-height: 472rpx;
  overflow: hidden;
  color: #ffffff;
  background: #172033;
}

.order-hero-img,
.order-hero-mask {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.order-hero-img {
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.order-hero-mask {
  background:
    linear-gradient(180deg, rgba(23, 32, 51, 0.62), rgba(217, 75, 52, 0.82)),
    radial-gradient(circle at 90% 15%, rgba(255, 210, 165, 0.45), transparent 26%);
}

.order-detail-nav,
.order-detail-status,
.detail-action-row {
  display: flex;
  align-items: center;
}

.order-detail-nav {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 84rpx minmax(0, 1fr) minmax(148rpx, auto);
  gap: 16rpx;
  padding: 88rpx 28rpx 0;
}

.order-detail-back {
  width: 60rpx;
  min-width: 60rpx;
  height: 60rpx;
  min-height: 60rpx;
  padding: 0;
  color: #d94b34 !important;
  background: rgba(255, 255, 255, 0.9) !important;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 46rpx;
  line-height: 50rpx;
}

.order-detail-nav b {
  color: #ffffff;
  font-size: 34rpx;
  font-weight: 900;
  text-align: center;
}

.order-detail-nav span {
  justify-self: end;
  max-width: 208rpx;
  padding: 8rpx 16rpx;
  overflow: hidden;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-detail-status {
  position: relative;
  z-index: 1;
  align-items: flex-start;
  flex-direction: column;
  padding: 48rpx 36rpx 40rpx;
}

.order-status {
  min-height: 42rpx;
  padding: 0 16rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.28);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 900;
  line-height: 42rpx;
}

.order-detail-status h2 {
  margin: 18rpx 0 0;
  font-size: 48rpx;
  font-weight: 900;
  line-height: 1.12;
}

.order-detail-status p {
  margin: 10rpx 0 0;
  font-size: 26rpx;
  line-height: 1.45;
  opacity: 0.9;
}

.order-detail-body {
  display: grid;
  gap: 24rpx;
  padding: 24rpx 24rpx 0;
}

.order-info-card {
  padding: 30rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 44rpx;
  box-shadow: 0 32rpx 60rpx rgba(126, 76, 49, 0.09);
}

.order-info-card h3 {
  margin: 0 0 16rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.order-info-card p {
  margin: 0;
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.45;
}

.review-pick-row {
  display: grid;
  grid-template-columns: 90rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: center;
  margin-top: 14rpx;
  padding: 16rpx;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
}

.review-pick-row.active {
  background: #fff1e9;
  border-color: #e85d3f;
}

.review-pick-thumb {
  display: grid;
  width: 86rpx;
  height: 86rpx;
  place-items: center;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7, #f05a37);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border-radius: 18rpx;
  font-weight: 900;
}

.review-pick-row b,
.review-pick-row span {
  display: block;
}

.review-pick-row b {
  overflow: hidden;
  color: #172033;
  font-size: 27rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.review-pick-row span {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 25rpx;
}

.star-row {
  display: flex;
  gap: 12rpx;
  margin: 18rpx 0;
}

.star-row text {
  color: #d9c8be;
  font-size: 58rpx;
  line-height: 1;
}

.star-row text.active {
  color: #f59e0b;
}

.review-image-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.review-image {
  display: grid;
  width: 104rpx;
  height: 104rpx;
  place-items: center;
  color: #c2412d;
  background: #fff1e9;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border: 1rpx solid #f2d6c4;
  border-radius: 18rpx;
  font-size: 26rpx;
  font-weight: 900;
}

.detail-action-row {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  gap: 16rpx;
  width: 100%;
  max-width: 390px;
  min-height: 132rpx;
  margin: 0 auto;
  padding: 16rpx 24rpx 28rpx;
  background: rgba(255, 250, 247, 0.98);
  border-top: 1rpx solid #f0dfd6;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
  backdrop-filter: blur(10rpx);
}

.detail-action-row button {
  flex: 1;
  min-height: 88rpx;
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 1rpx solid #f2d6c4;
  border-radius: 28rpx !important;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.detail-action-row button.primary {
  color: #ffffff !important;
  background: #e85d3f !important;
}
</style>
