<template>
  <view class="page checkout-page">
    <view class="checkout-head">
      <text class="checkout-kicker">结算确认</text>
      <text class="checkout-title">核对自提信息</text>
      <text class="checkout-sub">提交后生成待付款订单并锁定库存</text>
    </view>

    <view class="checkout-card pickup-card">
      <view class="card-title-row">
        <view>
          <text class="title">自提点</text>
          <text class="subtle">预计到店后核销提货</text>
        </view>
        <text class="pill">可提货</text>
      </view>
      <view class="info-row">
        <text>自提点</text>
        <text>{{ state.station.name }}</text>
      </view>
      <view class="info-row clickable" @click="openReceivers">
        <text>提货人</text>
        <text>{{ receiver?.name || '请选择提货人' }} ›</text>
      </view>
      <view class="info-row">
        <text>联系电话</text>
        <text>{{ receiver?.mobile || '-' }}</text>
      </view>
    </view>

    <view class="checkout-card">
      <view class="card-title-row">
        <view>
          <text class="title">商品清单</text>
          <text class="subtle">仅展示已选且有效商品</text>
        </view>
        <text class="pill">{{ cartItems.length }} 件</text>
      </view>
      <view v-for="item in cartItems" :key="item.id" class="checkout-item">
        <image v-if="item.image" class="checkout-thumb" :src="item.image" mode="aspectFill" />
        <view v-else class="checkout-thumb fallback">鲜</view>
        <view>
          <text class="checkout-name">{{ item.title }}</text>
          <text class="checkout-spec">{{ item.spec || '默认规格' }} · ×{{ item.qty }}</text>
        </view>
        <text class="checkout-price">¥{{ item.price }}</text>
      </view>
      <view v-if="cartItems.length === 0" class="empty-line">暂无可结算商品</view>
    </view>

    <view class="checkout-card">
      <view class="card-title-row">
        <view>
          <text class="title">订单备注</text>
          <text class="subtle">可填写提货说明，最多 50 字</text>
        </view>
      </view>
      <textarea v-model="remark" maxlength="50" placeholder="例如：傍晚到店提货" />
    </view>

    <view class="checkout-card">
      <view class="info-row">
        <text>商品金额</text>
        <text>¥{{ totalAmount }}</text>
      </view>
      <view class="info-row">
        <text>配送方式</text>
        <text>到店自提</text>
      </view>
      <view class="info-row total-line">
        <text>应付合计</text>
        <text>¥{{ totalAmount }}</text>
      </view>
    </view>

    <view class="checkout-bottom">
      <view>
        <text>实付</text>
        <text class="bottom-price">¥{{ totalAmount }}</text>
      </view>
      <button class="checkout-submit" :disabled="cartItems.length === 0 || submitting" @click="pay">
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
import { cartTotal, defaultReceiver, selectedCartItems, showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();
const remark = ref('');
const submitting = ref(false);
const cartItems = computed(() => selectedCartItems(state));
const receiver = computed(() => defaultReceiver());
const totalAmount = computed(() => cartTotal(state));

async function pay() {
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
        uni.navigateTo({ url: '/pages/login/index' });
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
        uni.navigateTo({ url: `/pages/payment/index?orderId=${order.id}` });
    } catch (error) {
        showUserToast('提交订单接口暂不可用，已进入支付演示页', 'warn');
        uni.navigateTo({ url: `/pages/payment/index?amount=${totalAmount.value}` });
    } finally {
        submitting.value = false;
    }
}

function openReceivers() {
    uni.navigateTo({ url: '/pages/receivers/index' });
}
</script>

<style lang="scss" scoped>
.checkout-page {
  min-height: 100vh;
  padding: 0 20rpx 226rpx;
  background:
    radial-gradient(circle at 88% 4%, rgba(255, 213, 166, 0.78), transparent 25%),
    linear-gradient(180deg, #172033 0%, #25304a 188rpx, #f7f1ea 190rpx, #f7f1ea 100%);
}

.checkout-head {
  padding: 46rpx 8rpx 26rpx;
  color: #ffffff;
}

.checkout-kicker,
.checkout-title,
.checkout-sub {
  display: block;
}

.checkout-kicker {
  color: rgba(255, 255, 255, 0.76);
  font-size: 22rpx;
  font-weight: 900;
}

.checkout-title {
  margin-top: 8rpx;
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.12;
}

.checkout-sub {
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.86);
  font-size: 24rpx;
}

.checkout-card {
  margin-bottom: 18rpx;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(126, 76, 49, 0.08);
}

.card-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  color: #172033;
  border-bottom: 1rpx solid #f4e2d8;
  font-size: 26rpx;
  font-weight: 800;
}

.info-row:last-child {
  border-bottom: 0;
}

.info-row.clickable text:last-child {
  color: #d94b34;
}

.info-row text:first-child {
  color: #8c6a58;
  font-weight: 700;
}

.total-line text:last-child {
  color: #d94b34;
  font-size: 32rpx;
  font-weight: 900;
}

.checkout-item {
  display: grid;
  grid-template-columns: 108rpx minmax(0, 1fr) auto;
  gap: 16rpx;
  align-items: center;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f4e2d8;
}

.checkout-item:last-child {
  border-bottom: 0;
}

.checkout-thumb {
  display: grid;
  width: 108rpx;
  height: 108rpx;
  place-items: center;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7 0%, #ffcf55 42%, #f05a37 100%);
  border-radius: 18rpx;
  font-size: 32rpx;
  font-weight: 900;
}

.checkout-name,
.checkout-spec {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
}

.checkout-name {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.28;
  white-space: nowrap;
}

.checkout-spec {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 22rpx;
  white-space: nowrap;
}

.checkout-price {
  color: #d94b34;
  font-size: 26rpx;
  font-weight: 900;
}

.empty-line {
  padding: 28rpx 0;
  color: #8c6a58;
  font-size: 24rpx;
}

.checkout-bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 232rpx;
  gap: 18rpx;
  align-items: center;
  min-height: 134rpx;
  padding: 18rpx 24rpx 28rpx;
  background: rgba(255, 255, 255, 0.97);
  border-top: 1rpx solid #f0dfd6;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
}

.checkout-bottom text:first-child {
  color: #172033;
  font-size: 24rpx;
  font-weight: 900;
}

.bottom-price {
  margin-left: 8rpx;
  color: #d94b34;
  font-size: 36rpx;
  font-weight: 900;
}

.checkout-submit {
  min-height: 82rpx;
  font-size: 28rpx;
}
</style>
