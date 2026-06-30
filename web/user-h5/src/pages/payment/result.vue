<template>
  <view class="pay-result-page shop-page" data-m-page="payResult">
    <view class="pay-result-hero">
      <view class="order-nav">
        <button class="order-back" @click="goHome">‹</button>
        <b>支付结果</b>
        <span>完成</span>
      </view>
      <view class="pay-result-status">
        <view class="pay-result-icon" :class="statusClass">{{ resultIcon }}</view>
        <h2>{{ resultTitle }}</h2>
        <p>{{ resultSub }}</p>
      </view>
    </view>

    <view class="pay-result-card">
      <h3>订单信息</h3>
      <view class="detail-field">
        <span>订单号</span>
        <b>{{ displayOrderNo }}</b>
      </view>
      <view class="detail-field">
        <span>实付金额</span>
        <b>¥{{ displayAmount }}</b>
      </view>
      <view class="detail-field">
        <span>自提点</span>
        <b>{{ order?.stationName || state.station.name }}</b>
      </view>
      <view class="detail-field">
        <span>提货日</span>
        <b>预计提货日 {{ pickupDate }}</b>
      </view>
      <view class="detail-field">
        <span>提货人</span>
        <b>{{ order?.pickupName || defaultReceiverName }}</b>
      </view>
      <view class="pay-result-actions">
        <button @click="goOrder">查看订单</button>
        <button v-if="status !== 'success'" class="primary" @click="retryPay">重新支付</button>
        <button v-else class="primary" @click="goHome">继续逛逛</button>
      </view>
    </view>

    <view class="recommend-section">
      <h3>支付后推荐 <span>这些也适合一起买</span></h3>
      <view class="recommend-grid">
        <view v-for="item in recommendProducts" :key="item.publishSkuId" class="recommend-card" @click="openProduct(item.productId)">
          <view class="recommend-img" :style="backgroundImageStyle(item.mainImageUrl)" />
          <b>{{ item.productName }}</b>
          <p>{{ productSpecText(item) }}</p>
          <view class="recommend-sale-meta">
            <span v-if="Number(item.soldQty || 0) > 0">已售{{ item.soldQty }}</span>
            <span><b>{{ item.availableQty <= 0 ? '已售罄' : `剩余${item.availableQty}` }}</b></span>
          </view>
          <view class="recommend-tag-row">
            <span class="recommend-date">提货日期 {{ productPickupText(item.deliveryDate) }}</span>
          </view>
          <view class="recommend-bottom">
            <view class="recommend-price-stack">
              <span>¥{{ item.salePrice }}</span>
              <em>¥{{ productOldPrice(item.salePrice) }}</em>
            </view>
            <button @click.stop="openProduct(item.productId)" aria-label="加入购物车">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M3.5 4.5h2.4L8 15.7h9.7l2.2-7.8H7.2"></path>
                <path d="M12 11h6"></path>
                <path d="M15 8v6"></path>
              </svg>
            </button>
          </view>
        </view>
      </view>
    </view>

    <UserTabBar active="home" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import UserTabBar from '@/components/UserTabBar.vue';
import { defaultReceiver, findLocalOrder, navigateUser, useUserState, type LocalOrder } from '@/stores/userState';
import { fallbackProductImages, fallbackProducts, productPickupText, productSpecText } from '@/utils/userFallbackData';
import { currentRouteQuery } from '@/utils/routeQuery';

const state = useUserState();
const status = ref('success');
const amount = ref('');
const orderId = ref(0);
const order = ref<LocalOrder>();

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
    const orderNo = displayOrderNo.value;
    const map: Record<string, string> = {
        success: `订单 ${orderNo} 已支付，商品已进入仓库备货中。`,
        failed: '支付未完成，可重新发起微信支付。',
        closed: '本次支付已关闭，订单仍可在有效期内继续支付。',
        processing: '支付结果需要等待微信回调，请稍后刷新订单。'
    };
    return map[status.value] || '请返回订单列表查看最新状态。';
});
const displayAmount = computed(() => order.value?.payAmount || amount.value || '0.00');
const displayOrderNo = computed(() => order.value?.orderNo || (orderId.value ? `本地演示订单 ${orderId.value}` : '本地演示订单'));
const statusClass = computed(() => status.value);
const pickupDate = computed(() => order.value?.items?.[0]?.expectedPickupDate || state.station.deliveryTime || '明日');
const defaultReceiverName = computed(() => defaultReceiver()?.name || '默认提货人');
const recommendProducts = computed(() => fallbackProducts.slice(0, 4));

function goOrder() {
    navigateUser('/pages/order/index?status=all', true);
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

function retryPay() {
    navigateUser(`/pages/payment/index?amount=${displayAmount.value}&orderId=${orderId.value || ''}`, true);
}

function openProduct(id: number) {
    navigateUser(`/pages/product/detail?id=${id}`);
}

function productOldPrice(value: string) {
    const price = Number(value || 0);
    return Number.isFinite(price) && price > 0 ? (price * 1.25).toFixed(2) : '';
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

onMounted(() => {
    const query = currentRouteQuery();
    status.value = query.status || 'success';
    amount.value = query.amount || '';
    orderId.value = Number(query.orderId || query.id || 0);
    order.value = findLocalOrder(orderId.value);
});
</script>

<style lang="scss" scoped>
.pay-result-page {
  min-height: 100vh;
  padding: 0 0 184rpx;
  background: #f7f1ea;
}

.pay-result-hero {
  padding: 104rpx 32rpx 36rpx;
  color: #ffffff;
  background:
    radial-gradient(circle at 86% 14%, rgba(255, 214, 172, 0.55), transparent 24%),
    linear-gradient(135deg, #172033, #d94b34);
}

.order-nav,
.pay-result-actions,
.detail-field,
.recommend-bottom,
.recommend-sale-meta,
.recommend-tag-row {
  display: flex;
  align-items: center;
}

.order-nav {
  justify-content: space-between;
  height: 58rpx;
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
  font-size: 26rpx;
  opacity: 0.88;
}

.pay-result-status {
  display: grid;
  justify-items: center;
  padding-top: 40rpx;
  text-align: center;
}

.pay-result-icon {
  display: grid;
  width: 128rpx;
  height: 128rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 50%;
  font-size: 72rpx;
  font-weight: 900;
  box-shadow: inset 0 0 0 1rpx rgba(255, 255, 255, 0.34);
  place-items: center;
}

.pay-result-icon.failed,
.pay-result-icon.closed {
  color: #b42318;
}

.pay-result-icon.processing {
  color: #ffffff;
}

.pay-result-status h2 {
  margin: 20rpx 0 0;
  font-size: 48rpx;
  font-weight: 900;
  line-height: 1.12;
}

.pay-result-status p {
  max-width: 580rpx;
  margin: 12rpx 0 0;
  font-size: 26rpx;
  line-height: 1.45;
  opacity: 0.88;
}

.pay-result-card,
.recommend-section {
  margin: 20rpx 20rpx 0;
  padding: 26rpx;
  background: #ffffff;
  border: 0;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.pay-result-card h3,
.recommend-section h3 {
  margin: 0 0 10rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.detail-field {
  justify-content: space-between;
  gap: 18rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f2dfd4;
}

.detail-field span {
  color: #8c6a58;
  font-size: 26rpx;
}

.detail-field b {
  min-width: 0;
  color: #172033;
  font-size: 25rpx;
  font-weight: 900;
  text-align: right;
}

.pay-result-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin-top: 24rpx;
}

.pay-result-actions button {
  min-height: 80rpx;
  border-radius: 999rpx !important;
  font-weight: 900;
}

.pay-result-actions button:first-child {
  color: #c2412d !important;
  background: #fff0e8 !important;
  border: 0;
  box-shadow: none;
}

.pay-result-actions button.primary {
  color: #ffffff !important;
  background: #e85d3f !important;
}

.recommend-section h3 {
  display: flex;
  justify-content: space-between;
}

.recommend-section h3 span {
  color: #8c6a58;
  font-size: 25rpx;
  font-weight: 600;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 18rpx;
}

.recommend-card {
  min-width: 0;
  padding: 14rpx;
  background: #fffaf6;
  border: 1rpx solid #f1dfd4;
  border-radius: 20rpx;
}

.recommend-img {
  width: 100%;
  height: 136rpx;
  border-radius: 16rpx;
  background: #fff4eb;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.recommend-card b,
.recommend-card p {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-card b {
  margin-top: 12rpx;
  color: #172033;
  font-size: 25rpx;
}

.recommend-card p {
  margin: 6rpx 0 0;
  color: #8c6a58;
  font-size: 26rpx;
}

.recommend-sale-meta,
.recommend-tag-row {
  flex-wrap: wrap;
  gap: 8rpx;
  margin-top: 10rpx;
}

.recommend-sale-meta span,
.recommend-date {
  display: inline-flex;
  align-items: center;
  min-height: 32rpx;
  padding: 0 10rpx;
  color: #4d7465;
  background: #f1faf4;
  border: 1rpx solid rgba(31, 138, 112, 0.18);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 800;
  white-space: nowrap;
}

.recommend-sale-meta b {
  margin: 0;
  color: #1f8a70;
  font-size: inherit;
}

.recommend-date {
  max-width: 100%;
  color: #9d4b2f;
  background: #fff4eb;
  border-color: #f3d4c1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recommend-bottom {
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 10rpx;
}

.recommend-price-stack {
  min-width: 0;
}

.recommend-price-stack span {
  display: block;
  color: #f20d0d;
  font-size: 30rpx;
  font-weight: 900;
}

.recommend-price-stack em {
  display: block;
  margin-top: 2rpx;
  color: #b9a89e;
  font-size: 26rpx;
  font-style: normal;
  font-weight: 800;
  text-decoration: line-through;
}

.recommend-bottom button {
  display: grid;
  width: 54rpx;
  min-width: 54rpx;
  height: 54rpx;
  min-height: 54rpx;
  padding: 0;
  background: #f20d0d !important;
  border-radius: 50% !important;
  place-items: center;
}

.recommend-bottom svg {
  width: 30rpx;
  height: 30rpx;
  fill: none;
  stroke: #ffffff;
  stroke-width: 2.2;
}
</style>
