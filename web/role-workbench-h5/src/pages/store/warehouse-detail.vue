<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>仓库汇总</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">仓库商品汇总</text>
      <text class="subtle">按仓库、商品和送货单查看当前供应商数据。</text>
    </view>

    <view class="section">
      <view class="summary-box">
        <view>
          <text>{{ orders.length }}</text>
          <text>送货单</text>
        </view>
        <view>
          <text>{{ qty }}</text>
          <text>数量</text>
        </view>
        <view>
          <text>¥{{ amount }}</text>
          <text>金额</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">送货单</text>
          <text class="subtle">点击可查看详情</text>
        </view>
      </view>
      <view class="task-list">
        <view v-for="order in orders" :key="order.no" class="task-card" @click="open(order.no)">
          <view class="task-main">
            <text class="task-title">{{ order.no }}</text>
            <text class="task-desc">{{ order.warehouse || order.station }} · {{ order.totalQty }} 件 · ¥{{ order.amount }}</text>
          </view>
          <text class="status-pill" :class="order.statusClass">{{ order.status }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { currentProfile } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const orders = computed(() => profile.value.orders);
const qty = computed(() => orders.value.reduce((sum, item) => sum + item.totalQty, 0));
const amount = computed(() => orders.value.reduce((sum, item) => sum + Number(item.amount.replace(/,/g, '')), 0).toFixed(2));

function open(no: string) {
    uni.navigateTo({ url: `/pages/orders/detail?no=${encodeURIComponent(no)}` });
}
</script>

<style lang="scss" scoped>
.summary-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
}

.summary-box view {
  padding: 18rpx;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
  text-align: center;
}

.summary-box text:first-child {
  display: block;
  color: #d94b34;
  font-size: 30rpx;
  font-weight: 900;
}

.summary-box text:last-child {
  color: #8f6c58;
  font-size: 22rpx;
}
</style>
