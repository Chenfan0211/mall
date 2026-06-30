<template>
  <view class="pay-page shop-page" data-m-page="pay">
    <view class="pay-head">
      <view class="order-nav">
        <button class="order-back" @click="goBack">‹</button>
        <b>订单支付</b>
        <span>{{ countdownText }}</span>
      </view>
      <view class="pay-title-block">
        <b>微信支付</b>
        <span>订单号 {{ order?.orderNo || '本地演示订单' }}</span>
      </view>
    </view>

    <view class="pay-body">
      <view class="pay-card pay-amount-card">
        <span>微信支付金额</span>
        <b>¥{{ amount }}</b>
      </view>

      <view v-if="processing" class="pay-processing-card">
        <b>支付处理中</b>
        <span>正在确认支付结果，请稍后查看订单状态。</span>
      </view>

      <view v-if="payFailed" class="pay-fail-tip">支付未完成，订单仍保留在待支付状态。</view>

      <view v-if="state.station.status !== 1" class="station-abnormal-card">
        <b>自提点状态异常</b>
        <span>{{ state.station.abnormalReason || '支付前请确认自提点仍可用。' }}</span>
      </view>

      <view class="pay-card">
        <h3>支付方式</h3>
        <view class="pay-method active">
          <view><i>微</i>微信支付</view>
          <span aria-label="已选择微信支付"></span>
        </view>
      </view>

    </view>

    <view class="pay-bottom">
      <view>
        <span>应付金额</span>
        <b>¥{{ amount }}</b>
      </view>
      <button class="pay-submit" @click="createPay">确认支付</button>
    </view>

    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { createPaymentTrade } from '@/api/user';
import { findLocalOrder, navigateUser, showUserToast, useUserState, type LocalOrder } from '@/stores/userState';
import { currentRouteQuery } from '@/utils/routeQuery';

const state = useUserState();
const order = ref<LocalOrder>();
const amountFromQuery = ref('');
const leftSeconds = ref(15 * 60);
const processing = ref(false);
const payFailed = ref(false);
let timer: ReturnType<typeof setInterval> | undefined;

const amount = computed(() => order.value?.payAmount || amountFromQuery.value || '0.00');
const countdownText = computed(() => {
    const minutes = Math.floor(leftSeconds.value / 60).toString().padStart(2, '0');
    const seconds = (leftSeconds.value % 60).toString().padStart(2, '0');
    return `${minutes}:${seconds}`;
});

function loadOrder() {
    const query = currentRouteQuery();
    const id = Number(query.orderId || query.id || 0);
    amountFromQuery.value = query.amount || '';
    order.value = findLocalOrder(id);
}

async function createPay() {
    processing.value = true;
    payFailed.value = false;
    if (!order.value) {
        finish('success');
        return;
    }
    try {
        await createPaymentTrade(order.value.id, { userId: state.user.id, payChannel: 1 });
        finish('success');
    } catch {
        processing.value = false;
        payFailed.value = true;
        showUserToast('支付接口暂不可用，请稍后重试', 'warn');
    }
}

function finish(status: string) {
    navigateUser(`/pages/payment/result?status=${status}&amount=${amount.value}&orderId=${order.value?.id || ''}`, true);
}

function goBack() {
    uni.navigateBack({
        fail() {
            navigateUser('/pages/checkout/index', true);
        }
    });
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
.pay-page {
  min-height: 100vh;
  padding-bottom: 224rpx;
  background:
    radial-gradient(circle at 88% 4%, rgba(255, 213, 166, 0.78), transparent 25%),
    linear-gradient(180deg, #172033 0%, #25304a 340rpx, #f7f1ea 342rpx, #f7f1ea 100%);
}

.pay-head {
  padding: 88rpx 28rpx 36rpx;
  color: #ffffff;
  background: transparent;
  border-radius: 0;
}

.order-nav,
.pay-method,
.pay-bottom {
  display: flex;
  align-items: center;
}

.order-nav {
  justify-content: space-between;
  height: 58rpx;
  margin-bottom: 36rpx;
}

.order-back {
  width: 58rpx;
  min-width: 58rpx;
  height: 58rpx;
  min-height: 58rpx;
  padding: 0;
  color: #172033 !important;
  background: rgba(255, 255, 255, 0.94) !important;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 46rpx;
  line-height: 50rpx;
}

.order-nav b {
  font-size: 34rpx;
  font-weight: 900;
}

.order-nav span {
  padding: 8rpx 14rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.16);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 900;
}

.pay-title-block {
  margin-top: 0;
}

.pay-title-block b,
.pay-title-block span {
  display: block;
}

.pay-title-block b {
  font-size: 50rpx;
  font-weight: 900;
  line-height: 1.1;
}

.pay-title-block span {
  margin-top: 12rpx;
  font-size: 26rpx;
  opacity: 0.88;
}

.pay-body {
  display: grid;
  gap: 24rpx;
  padding: 0 24rpx;
}

.pay-card,
.pay-processing-card,
.pay-fail-tip,
.station-abnormal-card {
  margin-bottom: 0;
  padding: 30rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 44rpx;
  box-shadow: 0 32rpx 60rpx rgba(23, 32, 51, 0.1);
}

.pay-amount-card {
  text-align: center;
}

.pay-amount-card span,
.pay-amount-card b {
  display: block;
}

.pay-amount-card span {
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 800;
}

.pay-amount-card b {
  margin-top: 14rpx;
  color: #f20d0d;
  font-size: 66rpx;
  font-weight: 900;
  line-height: 1;
}

.pay-processing-card,
.pay-fail-tip,
.station-abnormal-card {
  color: #7b5f51;
  background: #fff7f1;
}

.pay-processing-card b,
.station-abnormal-card b {
  display: block;
  color: #172033;
  font-size: 28rpx;
}

.pay-processing-card span,
.station-abnormal-card span {
  display: block;
  margin-top: 8rpx;
  font-size: 26rpx;
}

.pay-fail-tip,
.station-abnormal-card {
  color: #b42318;
  background: #fff1e9;
}

.pay-card h3 {
  margin: 0 0 18rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.pay-method {
  justify-content: space-between;
  gap: 24rpx;
  min-height: 98rpx;
  padding: 0;
  background: transparent;
  border: 0;
  border-bottom: 1rpx solid #f4e2d8;
  border-radius: 0;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.pay-method:last-child {
  border-bottom: 0;
}

.pay-method div {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.pay-method i {
  display: grid;
  width: 72rpx;
  height: 72rpx;
  margin-right: 0;
  color: #d94b34;
  background: #fff0e8;
  border-radius: 28rpx;
  font-style: normal;
  place-items: center;
}

.pay-method span {
  position: relative;
  width: 36rpx;
  height: 36rpx;
  overflow: hidden;
  color: transparent;
  border: 4rpx solid #d94b34;
  border-radius: 50%;
  font-size: 0;
}

.pay-method span::after {
  position: absolute;
  inset: 6rpx;
  background: #d94b34;
  border-radius: 50%;
  content: "";
}

.pay-bottom {
  position: fixed;
  left: 50%;
  right: auto;
  bottom: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 292rpx;
  align-items: center;
  gap: 20rpx;
  width: min(100%, 390px);
  max-width: 390px;
  min-height: calc(132rpx + env(safe-area-inset-bottom));
  margin: 0 auto;
  padding: 20rpx 28rpx calc(28rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  border-top: 0;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
  transform: translateX(-50%);
}

.pay-bottom span,
.pay-bottom b {
  display: block;
}

.pay-bottom span {
  color: #8c6a58;
  font-size: 25rpx;
}

.pay-bottom b {
  margin-top: 4rpx;
  color: #f20d0d;
  font-size: 38rpx;
  font-weight: 900;
}

.pay-submit {
  min-width: 0;
  min-height: 76rpx;
  border-radius: 36rpx !important;
  background: linear-gradient(135deg, #d94b34, #f28a42) !important;
  box-shadow: 0 20rpx 36rpx rgba(217, 75, 52, 0.22);
  font-size: 30rpx;
}
</style>
