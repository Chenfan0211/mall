<template>
  <view class="page pay-result-page">
    <view class="result-hero">
      <text class="result-kicker">支付结果</text>
      <view class="success-mark" :class="statusClass">{{ resultIcon }}</view>
      <text class="result-title">{{ resultTitle }}</text>
      <text class="result-sub">{{ resultSub }}</text>
    </view>

    <view class="result-card">
      <view class="result-row">
        <text>订单状态</text>
        <text>{{ orderStatus }}</text>
      </view>
      <view class="result-row">
        <text>提货方式</text>
        <text>到店自提</text>
      </view>
      <view class="result-row">
        <text>提醒</text>
        <text>{{ resultTip }}</text>
      </view>
    </view>

    <view class="result-actions">
      <button class="primary" @click="goOrder">查看订单</button>
      <button v-if="status !== 'success'" class="plain" @click="retryPay">重新支付</button>
      <button v-else class="plain" @click="goHome">继续逛逛</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

const status = ref('success');
const amount = ref('');
const resultIcon = computed(() => {
    const map: Record<string, string> = {
        success: '✓',
        failed: '!',
        closed: '×',
        processing: '…'
    };
    return map[status.value] || '?';
});
const resultTitle = computed(() => {
    const map: Record<string, string> = {
        success: '支付成功',
        failed: '支付失败',
        closed: '支付已关闭',
        processing: '支付处理中'
    };
    return map[status.value] || '支付状态未知';
});
const resultSub = computed(() => {
    const map: Record<string, string> = {
        success: '预计明日到自提点提货',
        failed: '支付未完成，可重新发起微信支付',
        closed: '本次支付已关闭，订单仍可在有效期内继续支付',
        processing: '支付结果需要等待微信回调，请稍后刷新订单'
    };
    return map[status.value] || '请返回订单列表查看最新状态';
});
const resultTip = computed(() => (status.value === 'success' ? '到货后会发送站内消息' : `支付金额 ¥${amount.value || '0.00'} 未最终确认`));
const orderStatus = computed(() => {
    if (status.value === 'success') {
        return '已付款，待履约';
    }
    if (status.value === 'processing') {
        return '支付处理中';
    }
    return '待付款';
});
const statusClass = computed(() => status.value);

function goOrder() {
    uni.switchTab({ url: '/pages/order/index' });
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}

function retryPay() {
    uni.redirectTo({ url: `/pages/payment/index?amount=${amount.value}` });
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    status.value = current.options?.status || 'success';
    amount.value = current.options?.amount || '';
});
</script>

<style lang="scss" scoped>
.pay-result-page {
  min-height: 100vh;
  padding: 0 20rpx 150rpx;
  background:
    radial-gradient(circle at 88% 4%, rgba(255, 213, 166, 0.78), transparent 25%),
    linear-gradient(180deg, #172033 0%, #25304a 390rpx, #f7f1ea 392rpx, #f7f1ea 100%);
}

.result-hero {
  display: grid;
  justify-items: center;
  padding: 52rpx 16rpx 46rpx;
  color: #ffffff;
  text-align: center;
}

.result-kicker,
.result-title,
.result-sub {
  display: block;
}

.result-kicker {
  color: rgba(255, 255, 255, 0.76);
  font-size: 22rpx;
  font-weight: 900;
}

.success-mark {
  display: grid;
  width: 132rpx;
  height: 132rpx;
  margin: 32rpx 0 24rpx;
  place-items: center;
  color: #d94b34;
  background: #ffffff;
  border-radius: 50%;
  font-size: 70rpx;
  font-weight: 900;
  box-shadow: 0 18rpx 46rpx rgba(0, 0, 0, 0.18);
}

.success-mark.failed,
.success-mark.closed {
  color: #b42318;
}

.success-mark.processing {
  color: #172033;
}

.result-title {
  color: #ffffff;
  font-size: 48rpx;
  font-weight: 900;
  line-height: 1.12;
}

.result-sub {
  margin-top: 12rpx;
  color: rgba(255, 255, 255, 0.86);
  font-size: 24rpx;
}

.result-card {
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(126, 76, 49, 0.08);
}

.result-row {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f4e2d8;
  font-size: 26rpx;
}

.result-row:last-child {
  border-bottom: 0;
}

.result-row text:first-child {
  color: #8c6a58;
  font-weight: 700;
}

.result-row text:last-child {
  color: #172033;
  font-weight: 900;
  text-align: right;
}

.result-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
  margin-top: 22rpx;
}

.result-actions button {
  min-height: 82rpx;
}

.result-actions .plain {
  color: #c2412d !important;
  background: #fff2e9 !important;
  border: 1rpx solid #f2d6c4;
  box-shadow: none;
}
</style>
