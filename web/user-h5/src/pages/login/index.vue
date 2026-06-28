<template>
  <view class="page no-tab login-page">
    <view class="login-hero">
      <view class="avatar-img">未</view>
      <view>
        <text class="login-title">登录鲜选到家</text>
        <text class="login-sub">登录后可查看订单、售后、收藏和消息。</text>
      </view>
    </view>

    <view class="login-card">
      <text class="title">微信授权登录</text>
      <text class="subtle">浏览商品不强制登录，加入购物车、结算和个人资产需要授权。</text>
      <label class="agreement-row" @click="state.agreementAccepted = !state.agreementAccepted">
        <view class="check-dot" :class="{ checked: state.agreementAccepted }">{{ state.agreementAccepted ? '✓' : '' }}</view>
        <text>我已阅读并同意《用户协议》和《隐私协议》</text>
      </label>
      <button class="primary" @click="login(true)">微信授权登录并授权手机号</button>
      <button class="plain" @click="login(false)">手机号授权失败，先继续浏览</button>
      <button class="plain" @click="goHome">先逛逛</button>
    </view>

    <UserToast />
  </view>
</template>

<script setup lang="ts">
import UserToast from '@/components/UserToast.vue';
import { loginUser, showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();

function login(authorizeMobile: boolean) {
    if (!state.agreementAccepted) {
        showUserToast('请先勾选用户协议和隐私协议', 'warn');
        return;
    }
    loginUser(authorizeMobile);
    uni.redirectTo({
        url: state.afterLoginUrl || '/pages/home/index',
        fail() {
            uni.switchTab({ url: '/pages/home/index' });
        }
    });
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 26rpx;
  padding: 44rpx 24rpx;
}

.login-hero {
  display: grid;
  grid-template-columns: 110rpx minmax(0, 1fr);
  gap: 20rpx;
  align-items: center;
  color: #ffffff;
  padding: 30rpx;
  background: linear-gradient(135deg, #d94b34, #f28a42);
  border-radius: 32rpx;
}

.avatar-img {
  display: grid;
  width: 110rpx;
  height: 110rpx;
  place-items: center;
  color: #d94b34;
  background: #ffffff;
  border-radius: 50%;
  font-size: 42rpx;
  font-weight: 900;
}

.login-title,
.login-sub {
  display: block;
}

.login-title {
  font-size: 40rpx;
  font-weight: 900;
}

.login-sub {
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.88);
  font-size: 24rpx;
}

.login-card {
  display: grid;
  gap: 18rpx;
  padding: 30rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 30rpx;
  box-shadow: 0 12rpx 28rpx rgba(126, 76, 49, 0.08);
}

.agreement-row {
  display: grid;
  grid-template-columns: 42rpx minmax(0, 1fr);
  gap: 14rpx;
  align-items: center;
  color: #6b5a50;
  font-size: 24rpx;
}

.check-dot {
  display: grid;
  width: 38rpx;
  height: 38rpx;
  place-items: center;
  color: #ffffff;
  background: #ffffff;
  border: 2rpx solid #e2cfc5;
  border-radius: 50%;
  font-size: 22rpx;
  font-weight: 900;
}

.check-dot.checked {
  border-color: #e85d3f;
  background: #e85d3f;
}
</style>
