<template>
  <view class="checkout-page shop-page" data-m-page="checkout">
    <view class="checkout-head">
      <view class="order-nav">
        <button class="order-back" @click="goBack">‹</button>
        <b>确认订单</b>
        <span>不返回购物车编辑</span>
      </view>
      <view class="checkout-title-block">
        <b>确认自提信息</b>
        <span>请先确认自提点、预计提货日和提货人，再提交订单。</span>
      </view>
    </view>

    <view class="checkout-body">
      <view v-if="!state.mobileAuthorized" class="phone-auth-card">
        <b>手机号未授权</b>
        <span>下单前需要完成手机号授权，用于自提点核销联系。</span>
        <button @click="openLogin">去授权</button>
      </view>

      <view v-if="state.station.status !== 1" class="station-abnormal-card">
        <b>当前自提点暂不可下单</b>
        <span>{{ state.station.abnormalReason || '请切换营业中的自提点后再提交订单。' }}</span>
      </view>

      <view class="checkout-card checkout-station-card">
        <view class="checkout-card-head">
          <h3>自提点</h3>
          <button @click="goEntry">切换</button>
        </view>
        <view class="checkout-station">
          <i>取</i>
          <view class="checkout-station-main">
            <b>{{ state.station.name }}</b>
            <span>{{ state.station.address }}</span>
            <span>提货人：<em>{{ receiver?.name || '请选择提货人' }} {{ receiver?.mobile || '' }}</em></span>
            <span>提货日期：<em>{{ firstPickupDate }}</em></span>
            <span class="checkout-hidden-business">{{ state.station.businessHours }}｜{{ state.station.deliveryTime || '次日到团点' }}</span>
            <view class="checkout-station-actions">
              <button @click="openReceivers">修改收货人</button>
            </view>
          </view>
        </view>
      </view>

      <view class="checkout-card">
        <view class="checkout-card-head">
          <h3>商品清单</h3>
          <span>{{ cartItems.length }} 件</span>
        </view>
        <view class="checkout-date-group">
          <view class="checkout-date-head">
            <b>预计提货日 {{ firstPickupDate }}</b>
            <span>到店自提</span>
          </view>
          <view v-for="item in cartItems" :key="item.id" class="checkout-product">
            <view class="checkout-product-img" :style="backgroundImageStyle(item.image)" />
            <view class="checkout-product-main">
              <h4>{{ item.title }}</h4>
              <p>{{ item.spec || '默认规格' }}</p>
              <span>限购 {{ item.limit || 99 }} · 库存 {{ item.stock || 99 }}</span>
            </view>
            <view class="checkout-product-price">
              <b>¥{{ item.price }}</b>
              <span>x{{ item.qty }}</span>
            </view>
          </view>
          <view v-if="cartItems.length === 0" class="empty-line">暂无可结算商品</view>
        </view>
      </view>

      <view class="checkout-card">
        <view class="checkout-card-head">
          <h3>订单备注</h3>
          <span>最多 80 字</span>
        </view>
        <textarea v-model="remark" maxlength="80" placeholder="选填，可填写提货备注或商品偏好" />
        <view class="checkout-remark-tip">备注非必填，提交订单时随订单一起保存，可在订单详情查看。</view>
      </view>

      <view class="checkout-card checkout-fee-card">
        <h3>费用明细</h3>
        <view class="checkout-line">
          <span>商品金额</span>
          <b>¥{{ totalAmount }}</b>
        </view>
        <view class="checkout-line total">
          <span>实付金额</span>
          <b>¥{{ totalAmount }}</b>
        </view>
      </view>
    </view>

    <view class="checkout-bottom">
      <view>
        <span>共 {{ totalQty }} 件，实付</span>
        <b>¥{{ totalAmount }}</b>
        <em :class="{ blocked: checkoutBlocked }">{{ checkoutDisabledReason }}</em>
      </view>
      <button
        class="checkout-submit"
        :class="{ disabled: checkoutBlocked || submitting }"
        :disabled="checkoutBlocked || submitting"
        @click="pay"
      >
        {{ submitting ? '提交中' : '提交订单' }}
      </button>
    </view>

    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { submitOrder } from '@/api/user';
import {
    cartTotal,
    defaultReceiver,
    navigateUser,
    selectedCartItems,
    showUserToast,
    useUserState
} from '@/stores/userState';

const state = useUserState();
const remark = ref('');
const submitting = ref(false);
const cartItems = computed(() => selectedCartItems(state));
const receiver = computed(() => defaultReceiver());
const totalAmount = computed(() => cartTotal(state));
const totalQty = computed(() => cartItems.value.reduce((sum, item) => sum + item.qty, 0));
const firstPickupDate = computed(() => cartItems.value.find((item) => item.pickupDate)?.pickupDate || state.station.deliveryTime || '明日');
const fallbackCheckoutImage = '/static/user-home/shop-detail.jpg';
const checkoutBlocked = computed(() => !state.mobileAuthorized || state.station.status !== 1 || cartItems.value.length === 0 || !receiver.value);
const checkoutDisabledReason = computed(() => {
    if (cartItems.value.length === 0) {
        return '不可提交原因：暂无可结算商品。';
    }
    if (!receiver.value) {
        return '不可提交原因：请先维护提货人。';
    }
    if (!state.mobileAuthorized) {
        return '不可提交原因：下单前需要完成手机号授权。';
    }
    if (state.station.status !== 1) {
        return `不可提交原因：${state.station.abnormalReason || '当前自提点不可下单。'}`;
    }
    return '当前可提交；提交后锁定库存，支付超时自动释放。';
});

async function pay() {
    if (submitting.value) {
        return;
    }
    if (cartItems.value.length === 0) {
        showUserToast('请选择需要结算的商品', 'warn');
        return;
    }
    if (!receiver.value) {
        showUserToast('请先维护提货人', 'warn');
        return;
    }
    if (!state.mobileAuthorized) {
        showUserToast('下单前需要完成手机号授权', 'warn');
        navigateUser('/pages/login/index');
        return;
    }
    if (state.station.status !== 1) {
        showUserToast('当前自提点不可下单，请先切换自提点', 'warn');
        return;
    }
    submitting.value = true;
    try {
        const order = await submitOrder({
            userId: state.user.id,
            cityId: state.city.id,
            stationId: state.station.id,
            cartItemIds: cartItems.value.map((item) => item.id),
            pickupName: receiver.value.name,
            pickupMobile: receiver.value.mobile,
            remark: remark.value,
            idempotentKey: `checkout-${Date.now()}`
        });
        navigateUser(`/pages/payment/index?orderId=${order.id}`);
    } catch {
        showUserToast('提交订单接口暂不可用，已进入支付演示页', 'warn');
        navigateUser(`/pages/payment/index?amount=${totalAmount.value}`);
    } finally {
        submitting.value = false;
    }
}

function openLogin() {
    navigateUser('/pages/login/index');
}

function openReceivers() {
    navigateUser('/pages/receivers/index');
}

function goEntry() {
    navigateUser('/pages/entry/index');
}

function goBack() {
    uni.navigateBack({
        fail() {
            navigateUser('/pages/cart/index', true);
        }
    });
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackCheckoutImage}")`
    };
}
</script>

<style lang="scss" scoped>
.checkout-page {
  min-height: 100vh;
  padding-bottom: 224rpx;
  background:
    radial-gradient(circle at 88% 4%, rgba(255, 213, 166, 0.78), transparent 25%),
    linear-gradient(180deg, #172033 0%, #25304a 340rpx, #f7f1ea 342rpx, #f7f1ea 100%);
}

.checkout-head {
  padding: 88rpx 28rpx 36rpx;
  color: #ffffff;
  background: transparent;
  border-radius: 0;
}

.order-nav,
.checkout-card-head,
.checkout-station,
.checkout-station-actions,
.checkout-date-head,
.checkout-product,
.checkout-line,
.checkout-bottom {
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
  font-size: 25rpx;
  opacity: 0.88;
}

.checkout-title-block {
  margin-top: 0;
}

.checkout-title-block b,
.checkout-title-block span {
  display: block;
}

.checkout-title-block b {
  font-size: 50rpx;
  font-weight: 900;
  line-height: 1.12;
}

.checkout-title-block span {
  max-width: 560rpx;
  margin-top: 12rpx;
  font-size: 26rpx;
  line-height: 1.45;
  color: rgba(255, 255, 255, 0.82);
}

.checkout-body {
  display: grid;
  gap: 24rpx;
  padding: 0 24rpx;
}

.phone-auth-card,
.station-abnormal-card,
.checkout-card {
  margin-bottom: 0;
  padding: 30rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 44rpx;
  box-shadow: 0 32rpx 60rpx rgba(23, 32, 51, 0.1);
}

.phone-auth-card,
.station-abnormal-card {
  display: grid;
  gap: 10rpx;
  color: #7b5f51;
  background: #fff7f1;
}

.phone-auth-card b,
.station-abnormal-card b {
  color: #172033;
  font-size: 28rpx;
}

.phone-auth-card span,
.station-abnormal-card span {
  font-size: 26rpx;
  line-height: 1.45;
}

.phone-auth-card button {
  justify-self: start;
  min-height: 56rpx;
  padding: 0 22rpx;
  font-size: 26rpx;
}

.station-abnormal-card {
  color: #b42318;
  background: #fff1e9;
}

.checkout-card-head {
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 18rpx;
}

.checkout-card-head h3 {
  margin: 0;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.checkout-card-head span {
  color: #8c6a58;
  font-size: 25rpx;
  font-weight: 800;
}

.checkout-card-head button,
.checkout-station-actions button {
  min-height: 50rpx;
  padding: 0 18rpx;
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 1rpx solid #f2d6c4;
  font-size: 25rpx;
  box-shadow: none;
}

.checkout-station {
  display: grid;
  grid-template-columns: 76rpx minmax(0, 1fr);
  align-items: flex-start;
  gap: 22rpx;
}

.checkout-station i {
  display: grid;
  width: 76rpx;
  min-width: 76rpx;
  height: 76rpx;
  color: #d94b34;
  background: #fff0e8;
  border-radius: 28rpx;
  font-style: normal;
  font-weight: 900;
  place-items: center;
}

.checkout-station-main {
  min-width: 0;
  flex: 1;
}

.checkout-station-main b,
.checkout-station-main span {
  display: block;
}

.checkout-station-main b {
  overflow: hidden;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.checkout-station-main span {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.42;
}

.checkout-station-main em {
  color: #172033;
  font-style: normal;
  font-weight: 900;
}

.checkout-hidden-business {
  color: #a06c52 !important;
}

.checkout-station-actions {
  margin-top: 14rpx;
}

.checkout-date-group {
  overflow: hidden;
  border: 1rpx solid #f2dfd4;
  border-radius: 20rpx;
}

.checkout-date-head {
  justify-content: space-between;
  gap: 12rpx;
  padding: 16rpx 18rpx;
  background: #fff7f1;
}

.checkout-date-head b {
  color: #172033;
  font-size: 25rpx;
}

.checkout-date-head span {
  color: #e85d3f;
  font-size: 25rpx;
  font-weight: 900;
}

.checkout-product {
  align-items: flex-start;
  gap: 16rpx;
  padding: 18rpx;
  border-top: 1rpx solid #f2dfd4;
}

.checkout-product-img {
  display: grid;
  width: 104rpx;
  min-width: 104rpx;
  height: 104rpx;
  color: #7c1d12;
  background: #fff4eb;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border-radius: 18rpx;
  font-weight: 900;
  place-items: center;
}

.checkout-product-main {
  min-width: 0;
  flex: 1;
}

.checkout-product-main h4 {
  overflow: hidden;
  margin: 0;
  color: #172033;
  font-size: 27rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.checkout-product-main p,
.checkout-product-main span {
  display: block;
  overflow: hidden;
  margin: 8rpx 0 0;
  color: #8c6a58;
  font-size: 25rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.checkout-product-price {
  text-align: right;
}

.checkout-product-price b,
.checkout-product-price span {
  display: block;
}

.checkout-product-price b {
  color: #f20d0d;
  font-size: 27rpx;
  font-weight: 900;
}

.checkout-product-price span {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 25rpx;
}

.empty-line {
  padding: 28rpx 18rpx;
  color: #8c6a58;
  font-size: 26rpx;
}

textarea {
  min-height: 156rpx;
  background: #fffaf6;
}

.checkout-remark-tip {
  margin-top: 12rpx;
  color: #8c6a58;
  font-size: 25rpx;
  line-height: 1.45;
}

.checkout-fee-card h3 {
  margin: 0 0 8rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.checkout-line {
  justify-content: space-between;
  gap: 16rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f2dfd4;
}

.checkout-line:last-child {
  border-bottom: 0;
}

.checkout-line span {
  color: #8c6a58;
  font-size: 26rpx;
}

.checkout-line b {
  color: #172033;
  font-size: 26rpx;
}

.checkout-line.total b {
  color: #f20d0d;
  font-size: 34rpx;
}

.checkout-bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 292rpx;
  align-items: center;
  gap: 20rpx;
  width: 100%;
  max-width: 390px;
  min-height: calc(132rpx + env(safe-area-inset-bottom));
  margin: 0 auto;
  padding: 20rpx 28rpx calc(28rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  border-top: 0;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
}

.checkout-bottom span,
.checkout-bottom b {
  display: block;
}

.checkout-bottom span {
  color: #8c6a58;
  font-size: 25rpx;
}

.checkout-bottom b {
  margin-top: 4rpx;
  color: #f20d0d;
  font-size: 38rpx;
  font-weight: 900;
}

.checkout-bottom em {
  display: block;
  max-width: 440rpx;
  margin-top: 6rpx;
  overflow: hidden;
  color: #8c6a58;
  font-size: 26rpx;
  font-style: normal;
  font-weight: 800;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.checkout-bottom em.blocked {
  color: #b42318;
}

.checkout-submit {
  min-width: 0;
  min-height: 76rpx;
  border-radius: 36rpx !important;
  background: linear-gradient(135deg, #d94b34, #f28a42) !important;
  box-shadow: 0 20rpx 36rpx rgba(217, 75, 52, 0.22);
  font-size: 30rpx;
}

.checkout-submit[disabled],
.checkout-submit.disabled {
  color: #9a8478 !important;
  background: #f4ece6 !important;
  box-shadow: none;
}
</style>
