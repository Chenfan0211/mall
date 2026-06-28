<template>
  <view class="page no-tab payment-page">
    <view class="pay-hero">
      <text class="pay-kicker">订单支付</text>
      <text class="pay-amount">¥{{ amount }}</text>
      <text class="pay-sub">请在 {{ leftSeconds }} 秒内完成微信支付</text>
    </view>

    <view class="section">
      <view class="row">
        <text>订单号</text>
        <text>{{ order?.orderNo || '本地演示订单' }}</text>
      </view>
      <view class="row">
        <text>支付方式</text>
        <text>微信支付</text>
      </view>
      <view class="row">
        <text>自提点</text>
        <text>{{ order?.stationName || state.station.name }}</text>
      </view>
    </view>

    <view class="section">
      <text class="title">支付状态演示</text>
      <text class="subtle">后端支付接口暂缺时，可在这里查看成功、失败、关闭、处理中状态。</text>
      <view class="pay-state-grid">
        <button class="primary" @click="finish('success')">支付成功</button>
        <button class="plain" @click="finish('processing')">处理中</button>
        <button class="plain" @click="finish('failed')">支付失败</button>
        <button class="plain" @click="finish('closed')">关闭支付</button>
      </view>
    </view>

    <button class="wechat-pay" @click="createPay">微信支付</button>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { createPaymentTrade } from '@/api/user';
import { findLocalOrder, showUserToast, useUserState, type LocalOrder } from '@/stores/userState';

const state = useUserState();
const order = ref<LocalOrder>();
const amountFromQuery = ref('');
const leftSeconds = ref(15 * 60);
let timer: ReturnType<typeof setInterval> | undefined;
const amount = computed(() => order.value?.payAmount || amountFromQuery.value || '0.00');

function loadOrder() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    const id = Number(current.options?.orderId || current.options?.id || 0);
    amountFromQuery.value = current.options?.amount || '';
    order.value = findLocalOrder(id);
}

async function createPay() {
    try {
        if (!order.value) {
            throw new Error('missing order');
        }
        await createPaymentTrade(order.value.id, { userId: state.user.id, payChannel: 1 });
        finish('success');
    } catch (error) {
        showUserToast('支付接口暂不可用，可使用状态演示按钮查看结果', 'warn');
    }
}

function finish(status: string) {
    uni.redirectTo({ url: `/pages/payment/result?status=${status}&amount=${amount.value}&orderId=${order.value?.id || ''}` });
}

onMounted(() => {
    loadOrder();
    timer = setInterval(() => {
        leftSeconds.value = Math.max(0, leftSeconds.value - 1);
        if (leftSeconds.value === 0 && timer) {
            clearInterval(timer);
        }
    }, 1000);
});

onUnmounted(() => {
    if (timer) {
        clearInterval(timer);
    }
});
</script>

<style lang="scss" scoped>
.payment-page {
  padding-bottom: 80rpx;
}

.pay-hero {
  display: grid;
  justify-items: center;
  margin: 0 -20rpx;
  padding: 54rpx 24rpx 48rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #172033, #25304a);
  text-align: center;
}

.pay-kicker,
.pay-amount,
.pay-sub {
  display: block;
}

.pay-kicker {
  color: rgba(255, 255, 255, 0.76);
  font-size: 22rpx;
  font-weight: 900;
}

.pay-amount {
  margin-top: 20rpx;
  color: #ffffff;
  font-size: 72rpx;
  font-weight: 900;
  line-height: 1;
}

.pay-sub {
  margin-top: 14rpx;
  color: rgba(255, 255, 255, 0.86);
  font-size: 24rpx;
}

.pay-state-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14rpx;
  margin-top: 18rpx;
}

.wechat-pay {
  width: calc(100% - 40rpx);
  margin: 24rpx 20rpx 0;
  background: #16a34a !important;
}
</style>
