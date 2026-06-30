<template>
  <view class="order-detail-page shop-page" data-m-page="orderDetail">
    <template v-if="order">
      <view class="order-detail-hero">
        <view class="order-hero-img" :style="backgroundImageStyle('/static/user-home/shop-home.jpg')" />
        <view class="order-hero-mask"></view>
        <view class="order-detail-nav">
          <button class="order-detail-back" @click="goOrder">‹</button>
          <b>订单详情</b>
          <span>{{ order.tradeStatus === 10 ? '待支付' : '详情' }}</span>
        </view>
        <view class="order-detail-status">
          <span class="order-status" :class="statusClass">{{ orderStatusTitle }}</span>
          <h2>{{ orderStatusTitle }}</h2>
          <p>{{ orderStatusDesc }}</p>
          <view v-if="order.tradeStatus === 10" class="pay-countdown">剩余支付时间 15:00</view>
        </view>
      </view>

      <view class="order-detail-body">
        <view class="order-info-card order-detail-pickup-card">
          <h3>提货信息</h3>
          <view class="pickup-info-plain">
            <b>{{ order.stationName }}</b>
            <span>{{ state.station.address }}</span>
            <span>提货时间：{{ order.items[0]?.expectedPickupDate || state.station.deliveryTime || '待定' }}</span>
            <span>提货人：{{ order.pickupName }} {{ order.pickupMobile }}</span>
          </view>
        </view>

        <view class="order-info-card">
          <h3>订单信息</h3>
          <view class="detail-field">
            <span>订单号</span>
            <b>{{ order.orderNo }}</b>
          </view>
          <view class="detail-field">
            <span>自提点</span>
            <b>{{ order.stationName }}</b>
          </view>
          <view class="detail-field">
            <span>提货人</span>
            <b>{{ order.pickupName }} {{ order.pickupMobile }}</b>
          </view>
          <view class="detail-field">
            <span>下单时间</span>
            <b>{{ order.createTime }}</b>
          </view>
          <view v-if="order.remark" class="detail-field">
            <span>订单备注</span>
            <b>{{ order.remark }}</b>
          </view>
        </view>

        <view class="order-info-card">
          <h3>商品信息</h3>
          <view v-for="item in order.items" :key="item.id" class="detail-product">
            <view class="order-thumb" :style="backgroundImageStyle(orderItemImage(item))" />
            <view class="order-product-main">
              <view class="order-product-date-row">预计提货日：<span>{{ item.expectedPickupDate || '待定' }}</span></view>
              <h4>{{ item.productName }}</h4>
              <p>{{ item.skuName }}</p>
              <view class="detail-price-line">
                <span class="order-product-price-qty"><b>¥{{ lineAmount(item) }}</b><em>x{{ item.qty }}</em></span>
                <span class="order-product-actions">
                  <button v-if="order.fulfillStatus === 60" @click="confirmReceive">确认收货</button>
                  <button v-if="canReviewOrder" @click="goReview">评价</button>
                  <button v-if="canAfterSaleOrder" @click="goAfterSale">申请售后</button>
                </span>
              </view>
            </view>
          </view>
        </view>

        <view class="order-info-card">
          <h3>履约进度</h3>
          <view v-for="item in order.timeline" :key="`${item.title}-${item.time}`" class="timeline-row">
            <view class="dot"></view>
            <view>
              <b>{{ item.title }}</b>
              <span>{{ item.content }}</span>
              <em>{{ item.time }}</em>
            </view>
          </view>
        </view>

        <view class="order-info-card">
          <h3>金额信息</h3>
          <view class="detail-field">
            <span>商品金额</span>
            <b>¥{{ order.totalAmount }}</b>
          </view>
          <view class="detail-field total">
            <span>实付金额</span>
            <b>¥{{ order.payAmount }}</b>
          </view>
        </view>
      </view>

      <view class="detail-action-row">
        <button @click="copyOrderNo">复制订单号</button>
        <button v-if="order.tradeStatus === 10" class="primary" @click="goPay">继续支付</button>
        <button v-else-if="order.fulfillStatus === 60" class="primary" @click="confirmReceive">确认收货</button>
        <button v-else-if="canReviewOrder" class="primary" @click="goReview">去评价</button>
        <button v-else @click="contactStation">联系自提点</button>
      </view>
    </template>

    <EmptyActionCard
      v-else
      title="未找到订单"
      sub="订单可能已同步到后台，请回订单列表刷新。"
      icon="单"
      button-text="返回订单"
      @action="goOrder"
    />

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
import { findLocalOrder, navigateUser, showUserToast, statusText, useUserState, type LocalOrder, type LocalOrderItem } from '@/stores/userState';
import { fallbackProductImages, fallbackProducts } from '@/utils/userFallbackData';
import { currentRouteQuery } from '@/utils/routeQuery';

const state = useUserState();
const order = ref<LocalOrder>();
const confirmVisible = ref(false);

const orderStatusTitle = computed(() => {
    if (!order.value) {
        return '订单状态待同步';
    }
    if (order.value.tradeStatus === 10) {
        return '待付款';
    }
    if (order.value.tradeStatus === 40 || order.value.fulfillStatus === 40) {
        return '售后处理中';
    }
    return statusText('fulfill', order.value.fulfillStatus);
});
const orderStatusDesc = computed(() => {
    if (!order.value) {
        return '';
    }
    if (order.value.tradeStatus === 10) {
        return '请尽快完成支付，超时后库存会释放。';
    }
    if (order.value.fulfillStatus === 60) {
        return '商品已到自提点，请凭手机号到店提货。';
    }
    if (order.value.fulfillStatus === 70) {
        return '订单已完成，可评价或申请售后。';
    }
    return '订单正在履约中，请关注履约进度。';
});
const statusClass = computed(() => {
    if (!order.value) {
        return 'preparing';
    }
    if (order.value.tradeStatus === 10) {
        return 'unpaid';
    }
    if (order.value.tradeStatus === 40 || order.value.fulfillStatus === 40) {
        return 'rejected';
    }
    if (order.value.fulfillStatus === 60) {
        return 'pickup';
    }
    if (order.value.fulfillStatus === 70) {
        return 'done';
    }
    return 'preparing';
});
const canReviewOrder = computed(() => order.value?.tradeStatus === 50 && order.value?.fulfillStatus === 70);
const canAfterSaleOrder = computed(() => order.value?.tradeStatus === 50 && order.value?.fulfillStatus === 70);

function loadOrder() {
    const query = currentRouteQuery();
    const id = Number(query.id || query.orderId || 0);
    order.value = findLocalOrder(id) || state.localOrders[0];
}

function lineAmount(item: LocalOrderItem) {
    return (Number(item.salePrice || 0) * Number(item.qty || 1)).toFixed(2);
}

function orderItemImage(item: LocalOrderItem) {
    return item.image || fallbackProducts.find((product) => product.productId === item.productId)?.mainImageUrl || fallbackProductImages[0];
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
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

function goAfterSale() {
    if (order.value) {
        navigateUser(`/pages/aftersale/apply?orderId=${order.value.id}`);
    }
}

function contactStation() {
    showUserToast('已复制自提点电话，可在真实小程序唤起拨号');
}

function goOrder() {
    navigateUser('/pages/order/index', true);
}

onMounted(loadOrder);
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: 100vh;
  padding-bottom: 176rpx;
  background: #f7f1ea;
}

.order-detail-hero {
  position: relative;
  min-height: 356rpx;
  overflow: hidden;
  color: #ffffff;
  background: #172033;
  border-radius: 0;
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
.detail-field,
.detail-product,
.detail-price-line,
.order-product-actions,
.timeline-row,
.detail-action-row,
.pickup-info-plain {
  display: flex;
  align-items: center;
}

.order-detail-nav {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 72rpx minmax(0, 1fr) minmax(128rpx, auto);
  justify-content: initial;
  gap: 16rpx;
  height: auto;
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
  min-width: 0;
  text-align: center;
  font-size: 34rpx;
  font-weight: 900;
}

.order-detail-nav span {
  justify-self: end;
  max-width: 208rpx;
  padding: 8rpx 16rpx;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  opacity: 1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-detail-status {
  position: relative;
  left: auto;
  right: auto;
  bottom: auto;
  z-index: 1;
  align-items: flex-start;
  flex-direction: column;
  padding: 34rpx 32rpx 30rpx;
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

.order-status.done {
  background: rgba(22, 163, 74, 0.82);
}

.order-status.rejected {
  background: rgba(180, 35, 24, 0.82);
}

.order-detail-status h2 {
  margin: 14rpx 0 0;
  font-size: 40rpx;
  font-weight: 900;
  line-height: 1.12;
}

.order-detail-status p {
  margin: 10rpx 0 0;
  font-size: 26rpx;
  line-height: 1.45;
  opacity: 0.9;
}

.pay-countdown {
  margin-top: 14rpx;
  padding: 8rpx 14rpx;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 900;
}

.order-detail-body {
  display: grid;
  gap: 16rpx;
  padding: 18rpx 24rpx 0;
}

.order-info-card {
  margin-bottom: 0;
  padding: 24rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(126, 76, 49, 0.07);
}

.order-info-card h3 {
  margin: 0 0 10rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.pickup-info-plain {
  align-items: flex-start;
  flex-direction: column;
  gap: 8rpx;
  margin-top: 14rpx;
  padding: 16rpx;
  background: #fff8f2;
  border-radius: 18rpx;
}

.pickup-info-plain b {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.pickup-info-plain span {
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.42;
}

.detail-field {
  justify-content: space-between;
  gap: 18rpx;
  padding: 14rpx 0;
  border-bottom: 1rpx solid #f2dfd4;
}

.detail-field:last-child {
  border-bottom: 0;
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

.detail-field.total b {
  color: #f20d0d;
  font-size: 34rpx;
}

.detail-product {
  align-items: flex-start;
  gap: 16rpx;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f2dfd4;
}

.detail-product:last-child {
  border-bottom: 0;
}

.order-thumb {
  width: 96rpx;
  min-width: 96rpx;
  height: 96rpx;
  border-radius: 16rpx;
  background: #fff4eb;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.order-product-main {
  min-width: 0;
  flex: 1;
}

.order-product-date-row {
  color: #8c6a58;
  font-size: 26rpx;
}

.order-product-date-row span {
  color: #e85d3f;
  font-weight: 900;
}

.order-product-main h4,
.order-product-main p {
  overflow: hidden;
  margin: 8rpx 0 0;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-product-main h4 {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.order-product-main p {
  color: #8c6a58;
  font-size: 26rpx;
}

.detail-price-line {
  justify-content: space-between;
  gap: 12rpx;
  margin-top: 12rpx;
}

.order-product-price-qty b {
  color: #f20d0d;
  font-size: 28rpx;
}

.order-product-price-qty em {
  margin-left: 8rpx;
  color: #8c6a58;
  font-style: normal;
  font-size: 25rpx;
}

.order-product-actions {
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 8rpx;
}

.order-product-actions button {
  min-height: 48rpx;
  padding: 0 14rpx;
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 1rpx solid #f2d6c4;
  font-size: 26rpx;
  box-shadow: none;
}

.timeline-row {
  align-items: flex-start;
  gap: 14rpx;
  padding-top: 18rpx;
}

.dot {
  width: 18rpx;
  min-width: 18rpx;
  height: 18rpx;
  margin-top: 8rpx;
  background: #e85d3f;
  border-radius: 50%;
}

.timeline-row b,
.timeline-row span,
.timeline-row em {
  display: block;
}

.timeline-row b {
  color: #172033;
  font-size: 26rpx;
}

.timeline-row span,
.timeline-row em {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 25rpx;
  font-style: normal;
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
  min-height: 76rpx;
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 1rpx solid #f2d6c4;
  border-radius: 24rpx !important;
  font-size: 26rpx;
  box-shadow: none;
  font-weight: 900;
}

.detail-action-row button.primary {
  color: #ffffff !important;
  background: #e85d3f !important;
}
</style>
