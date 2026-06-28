<template>
  <view class="page no-tab order-detail-page">
    <view class="dark-head">
      <text class="detail-kicker">订单详情</text>
      <text class="detail-title">{{ order?.orderNo || '订单不存在' }}</text>
      <text class="detail-sub">{{ orderStatus }}</text>
    </view>

    <view v-if="order" class="section">
      <view class="row">
        <text>自提点</text>
        <text>{{ order.stationName }}</text>
      </view>
      <view class="row">
        <text>提货人</text>
        <text>{{ order.pickupName }} {{ order.pickupMobile }}</text>
      </view>
      <view class="row">
        <text>下单时间</text>
        <text>{{ order.createTime }}</text>
      </view>
      <view class="row">
        <text>订单备注</text>
        <text>{{ order.remark || '无' }}</text>
      </view>
    </view>

    <view v-if="order" class="section">
      <view class="card-title-row">
        <view>
          <text class="title">商品清单</text>
          <text class="subtle">售后按订单商品发起</text>
        </view>
      </view>
      <view v-for="item in order.items" :key="item.id" class="detail-item">
        <view class="item-thumb">鲜</view>
        <view>
          <text>{{ item.productName }}</text>
          <text>{{ item.skuName }} · ×{{ item.qty }} · 预计 {{ item.expectedPickupDate }}</text>
        </view>
        <text>¥{{ item.salePrice }}</text>
      </view>
    </view>

    <view v-if="order" class="section">
      <text class="title">履约进度</text>
      <view v-for="item in order.timeline" :key="`${item.title}-${item.time}`" class="timeline-row">
        <view class="dot"></view>
        <view>
          <text>{{ item.title }}</text>
          <text>{{ item.content }}</text>
          <text>{{ item.time }}</text>
        </view>
      </view>
    </view>

    <view v-if="order" class="section">
      <view class="row">
        <text>商品金额</text>
        <text>¥{{ order.totalAmount }}</text>
      </view>
      <view class="row total-row">
        <text>实付金额</text>
        <text>¥{{ order.payAmount }}</text>
      </view>
    </view>

    <EmptyActionCard
      v-if="!order"
      title="未找到订单"
      sub="订单可能已同步到后台，请回订单列表刷新。"
      icon="单"
      button-text="返回订单"
      @action="goOrder"
    />

    <view v-if="order" class="fixed-actions">
      <button class="plain" @click="copyOrderNo">复制订单号</button>
      <button v-if="order.tradeStatus === 10" class="primary" @click="goPay">继续支付</button>
      <button v-else-if="order.fulfillStatus === 60" class="primary" @click="confirmReceive">确认收货</button>
      <button v-else-if="order.fulfillStatus === 70" class="primary" @click="goReview">去评价</button>
      <button v-else class="plain" @click="contactStation">联系自提点</button>
    </view>

    <ConfirmDialog
      :visible="confirmVisible"
      title="确认收货"
      content="确认已从自提点拿到商品？确认后订单进入已完成。"
      confirm-text="确认收货"
      @cancel="confirmVisible = false"
      @confirm="finishReceive"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserToast from '@/components/UserToast.vue';
import { findLocalOrder, navigateUser, showUserToast, statusText, type LocalOrder } from '@/stores/userState';

const order = ref<LocalOrder>();
const confirmVisible = ref(false);
const orderStatus = computed(() => {
    if (!order.value) {
        return '订单状态待同步';
    }
    return `${statusText('trade', order.value.tradeStatus)} · ${statusText('fulfill', order.value.fulfillStatus)}`;
});

function loadOrder() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    const id = Number(current.options?.id || current.options?.orderId || 0);
    order.value = findLocalOrder(id);
}

function copyOrderNo() {
    if (!order.value) {
        return;
    }
    uni.setClipboardData({
        data: order.value.orderNo,
        success() {
            showUserToast('订单号已复制');
        }
    });
}

function goPay() {
    if (order.value) {
        navigateUser(`/pages/payment/index?orderId=${order.value.id}`);
    }
}

function confirmReceive() {
    confirmVisible.value = true;
}

function finishReceive() {
    confirmVisible.value = false;
    showUserToast('确认收货接口暂缺，已保留页面状态', 'warn');
}

function goReview() {
    if (order.value) {
        navigateUser(`/pages/order/review?id=${order.value.id}`);
    }
}

function contactStation() {
    showUserToast('已复制自提点电话，可在真实小程序唤起拨号');
}

function goOrder() {
    uni.switchTab({ url: '/pages/order/index' });
}

onMounted(loadOrder);
</script>

<style lang="scss" scoped>
.order-detail-page {
  padding-bottom: 168rpx;
}

.detail-kicker,
.detail-title,
.detail-sub {
  display: block;
}

.detail-kicker {
  color: rgba(255, 255, 255, 0.76);
  font-size: 22rpx;
  font-weight: 900;
}

.detail-title {
  margin-top: 8rpx;
  color: #ffffff;
  font-size: 40rpx;
  font-weight: 900;
}

.detail-sub {
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.86);
  font-size: 24rpx;
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.detail-item {
  display: grid;
  grid-template-columns: 92rpx minmax(0, 1fr) auto;
  gap: 16rpx;
  align-items: center;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f4e2d8;
}

.detail-item:last-child {
  border-bottom: 0;
}

.item-thumb {
  display: grid;
  width: 92rpx;
  height: 92rpx;
  place-items: center;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7, #f05a37);
  border-radius: 18rpx;
  font-weight: 900;
}

.detail-item view:nth-child(2) {
  min-width: 0;
}

.detail-item view:nth-child(2) text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-item view:nth-child(2) text:first-child {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.detail-item view:nth-child(2) text:last-child {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.detail-item > text {
  color: #d94b34;
  font-size: 26rpx;
  font-weight: 900;
}

.timeline-row {
  display: grid;
  grid-template-columns: 28rpx minmax(0, 1fr);
  gap: 14rpx;
  padding-top: 18rpx;
}

.dot {
  width: 18rpx;
  height: 18rpx;
  margin-top: 8rpx;
  background: #e85d3f;
  border-radius: 50%;
}

.timeline-row text {
  display: block;
}

.timeline-row text:first-child {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.timeline-row text:not(:first-child) {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.total-row text:last-child {
  color: #d94b34;
  font-size: 32rpx;
  font-weight: 900;
}

.fixed-actions {
  position: fixed;
  left: 50%;
  bottom: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  width: min(100vw, 390px);
  padding: 18rpx 24rpx 28rpx;
  background: rgba(255, 255, 255, 0.97);
  border-top: 1rpx solid #f0dfd6;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
  transform: translateX(-50%);
}
</style>
