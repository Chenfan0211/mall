<template>
  <view class="page no-tab aftersale-apply-page">
    <view class="warm-head">
      <text class="head-kicker">申请售后</text>
      <text class="head-title">{{ order?.orderNo || '选择售后商品' }}</text>
      <text class="head-sub">支持仅退款、退货退款和部分退款。</text>
    </view>

    <view v-if="order" class="section">
      <text class="title">售后商品</text>
      <view v-for="item in order.items" :key="item.id" class="after-item" :class="{ active: selectedItemIds.includes(item.id) }" @click="toggleItem(item.id)">
        <view class="check-dot" :class="{ checked: selectedItemIds.includes(item.id) }"></view>
        <view>
          <text>{{ item.productName }}</text>
          <text>{{ item.skuName }} · 可售后 {{ item.refundableQty }} 件</text>
        </view>
        <text>¥{{ item.salePrice }}</text>
      </view>
    </view>

    <view class="section">
      <text class="title">售后类型</text>
      <view class="chip-row">
        <text v-for="item in typeTabs" :key="item.value" class="pill" :class="{ active: afterSaleType === item.value }" @click="afterSaleType = item.value">
          {{ item.label }}
        </text>
      </view>
    </view>

    <view class="section">
      <view class="row">
        <text>退款金额</text>
        <input v-model="refundAmount" type="digit" placeholder="请输入退款金额" />
      </view>
      <text class="title reason-title">售后原因</text>
      <textarea v-model="reason" maxlength="200" placeholder="请说明破损、少件、不新鲜等情况" />
    </view>

    <button class="submit-btn" :disabled="!canSubmit" @click="submitApply">提交售后申请</button>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { findLocalOrder, showUserToast, type LocalOrder } from '@/stores/userState';

const order = ref<LocalOrder>();
const selectedItemIds = ref<number[]>([]);
const afterSaleType = ref(1);
const refundAmount = ref('');
const reason = ref('');
const typeTabs = [
    { label: '仅退款', value: 1 },
    { label: '退货退款', value: 2 }
];
const canSubmit = computed(() => selectedItemIds.value.length > 0 && Number(refundAmount.value) > 0 && reason.value.trim().length >= 4);

function loadOrder() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    const id = Number(current.options?.orderId || current.options?.id || 0);
    order.value = findLocalOrder(id);
    const first = order.value?.items[0];
    if (first) {
        selectedItemIds.value = [first.id];
        refundAmount.value = first.salePrice;
    }
}

function toggleItem(id: number) {
    selectedItemIds.value = selectedItemIds.value.includes(id)
        ? selectedItemIds.value.filter((item) => item !== id)
        : [...selectedItemIds.value, id];
}

function submitApply() {
    if (!canSubmit.value) {
        showUserToast('请选择商品并填写售后原因', 'warn');
        return;
    }
    showUserToast('售后申请接口暂缺，页面已保留申请状态', 'warn');
    setTimeout(() => {
        uni.redirectTo({ url: '/pages/aftersale/index' });
    }, 600);
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

.after-item {
  display: grid;
  grid-template-columns: 40rpx minmax(0, 1fr) auto;
  gap: 14rpx;
  align-items: center;
  margin-top: 16rpx;
  padding: 16rpx;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
}

.after-item.active {
  border-color: #e85d3f;
  background: #fff1e9;
}

.check-dot {
  display: grid;
  width: 34rpx;
  height: 34rpx;
  place-items: center;
  background: #ffffff;
  border: 2rpx solid #e2cfc5;
  border-radius: 50%;
}

.check-dot.checked {
  border-color: #e85d3f;
  background: radial-gradient(circle at center, #ffffff 0 28%, #e85d3f 30% 100%);
}

.after-item view:nth-child(2) text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.after-item view:nth-child(2) text:first-child {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.after-item view:nth-child(2) text:last-child {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.after-item > text {
  color: #d94b34;
  font-size: 26rpx;
  font-weight: 900;
}

.reason-title {
  margin: 20rpx 0 14rpx;
}

.submit-btn {
  width: calc(100% - 40rpx);
  margin: 22rpx 20rpx 0;
}
</style>
