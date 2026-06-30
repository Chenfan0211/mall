<template>
  <view class="page shop-login-page shop-page" data-m-page="login">
    <view class="status-bar">
      <text>14:49</text>
      <text>▮▮▮ WiFi 80</text>
    </view>
    <view class="shop-login-card">
      <h3>登录鲜选到家</h3>
      <p>登录后可查看订单、售后、收藏和消息。</p>

      <label class="login-agreement-row" @click="state.agreementAccepted = !state.agreementAccepted">
        <view class="login-check" :class="{ checked: state.agreementAccepted }">{{ state.agreementAccepted ? '✓' : '' }}</view>
        <span>我已阅读并同意《用户协议》和《隐私协议》</span>
      </label>

      <button class="login-main-btn" @click="login(true)">微信授权登录并授权手机号</button>
      <button class="plain" @click="goHome">先逛逛</button>
    </view>

    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { loginUser, navigateUser, showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();

function login(authorizeMobile: boolean) {
    if (!state.agreementAccepted) {
        showUserToast('请先勾选用户协议和隐私协议', 'warn');
        return;
    }
    loginUser(authorizeMobile);
    navigateUser(state.afterLoginUrl || '/pages/home/index', true);
}

function goHome() {
    navigateUser('/pages/home/index', true);
}
</script>

<style lang="scss" scoped>
.shop-login-page {
  position: relative;
  min-height: 100vh;
  padding: 0 0 calc(180rpx + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(84, 42, 24, 0.38) 0%, rgba(84, 42, 24, 0.18) 30%, rgba(247, 241, 234, 0.62) 43%, #f7f1ea 100%),
    url("/static/user-home/login-market.svg") center top / 100% 520rpx no-repeat,
    #f7f1ea;
}

.status-bar {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 68rpx;
  padding: 0 48rpx;
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 800;
}

.shop-login-card {
  position: relative;
  z-index: 2;
  display: grid;
  width: min(652rpx, calc(100% - 96rpx));
  gap: 0;
  margin: 436rpx auto 0;
  padding: 44rpx 36rpx 36rpx;
  text-align: center;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(255, 255, 255, 0.72);
  border-radius: 40rpx;
  box-shadow: 0 36rpx 76rpx rgba(126, 76, 49, 0.18);
  backdrop-filter: blur(12rpx);
}

.shop-login-card::before {
  display: inline-flex;
  align-items: center;
  justify-self: center;
  min-height: 52rpx;
  margin-bottom: 24rpx;
  padding: 0 20rpx;
  color: #d94b34;
  content: "鲜选到家";
  background: #fff3ea;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
}

.shop-login-card h3,
.shop-login-card p {
  margin: 0;
}

.shop-login-card h3 {
  margin-bottom: 14rpx;
  color: #172033;
  font-size: 48rpx;
  font-weight: 900;
  line-height: 1.15;
  text-align: center;
}

.shop-login-card p {
  margin-bottom: 36rpx;
  color: #7b4a32;
  font-size: 26rpx;
  line-height: 1.45;
  text-align: center;
}

.login-agreement-row {
  display: grid;
  grid-template-columns: 36rpx minmax(0, 1fr);
  column-gap: 16rpx;
  align-items: center;
  margin: 0 0 36rpx;
  padding: 20rpx 22rpx;
  color: #172033;
  background: #fffaf6;
  border-radius: 24rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.35;
  text-align: left;
}

.login-check {
  display: grid;
  width: 38rpx;
  height: 38rpx;
  place-items: center;
  color: #ffffff;
  background: #ffffff;
  border: 2rpx solid #e6d4c9;
  border-radius: 50%;
  font-size: 25rpx;
  font-weight: 900;
}

.login-check.checked {
  background: #e85d3f;
  border-color: #e85d3f;
}

.login-main-btn,
.shop-login-card .plain {
  width: 100%;
  min-height: 92rpx;
  border: 0;
  border-radius: 999rpx;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1;
}

.login-main-btn {
  display: flex !important;
  align-items: center;
  justify-content: center;
  color: #ffffff !important;
  background: linear-gradient(135deg, #e85d3f, #f28a42) !important;
  box-shadow: 0 20rpx 44rpx rgba(232, 93, 63, 0.22);
}

.shop-login-card .plain {
  margin-top: 20rpx;
  color: #c2412d !important;
  background: #fff0e8 !important;
  box-shadow: none;
}
</style>
