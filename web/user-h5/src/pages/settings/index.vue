<template>
  <view class="service-page shop-page settings-page" data-m-page="settings">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>设置</b>
        <span>账号安全</span>
      </view>
    </view>

    <view class="service-list">
      <view class="settings-card">
        <view>
          <b>账号信息</b>
          <span>微信昵称 {{ state.user.nickname }}<br />手机号 {{ state.mobileAuthorized ? state.user.mobile : '未授权' }}</span>
        </view>
        <button class="service-action used" @click="goMine">返回</button>
      </view>

      <view class="settings-card">
        <view>
          <b>协议与隐私</b>
          <span>查看用户协议和隐私协议。</span>
        </view>
      </view>

      <view class="account-cancel-card account-cancel-risk">
        <view>
          <b>注销账号</b>
          <span>注销后当前账号将不能继续下单。</span>
        </view>
        <view class="account-cancel-limit">
          <b>暂不可注销</b>
          <span>存在未完成订单和售后中订单。</span>
          <span>待提货订单 DD202606180126；售后单 AS202606180021。</span>
        </view>
        <button @click="openCancelPanel">申请注销账号</button>
      </view>
    </view>

    <view v-if="cancelPanelVisible" class="account-cancel-mask" @click="cancelPanelVisible = false">
      <view class="account-cancel-panel" @click.stop>
        <h3>确认注销账号</h3>
        <p>当前账号注销后不能继续下单。</p>
        <view class="account-cancel-limit">
          <b>暂不可注销</b>
          <span>未完成订单：DD202606180126 待提货。</span>
          <span>售后中订单：AS202606180021 待退货。</span>
        </view>
        <view class="clear-confirm-actions">
          <button @click="cancelPanelVisible = false">再想想</button>
          <button class="danger" @click="logoutAnyway">仍要注销</button>
        </view>
      </view>
    </view>
    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';

import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { logoutUser, navigateUser, useUserState } from '@/stores/userState';

const state = useUserState();
const cancelPanelVisible = ref(false);

function openCancelPanel() {
    cancelPanelVisible.value = true;
}

function logoutAnyway() {
    cancelPanelVisible.value = false;
    logoutUser();
    navigateUser('/pages/login/index', true);
}

function goMine() {
    navigateUser('/pages/mine/index', true);
}
</script>

<style lang="scss" scoped>
.account-cancel-card {
  display: grid;
  gap: 16rpx;
  padding: 24rpx;
  background: #ffffff;
  border: 1rpx solid #f1d8cb;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.account-cancel-card > view:first-child b,
.account-cancel-card > view:first-child span {
  display: block;
}

.account-cancel-card > view:first-child b {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.account-cancel-card > view:first-child span {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.45;
}

.account-cancel-card > button,
.account-cancel-card > :deep(uni-button) {
  min-height: 70rpx;
  color: #ffffff !important;
  background: #b42318 !important;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.account-cancel-limit {
  display: grid;
  gap: 8rpx;
  padding: 18rpx;
  color: #b42318;
  background: #fff1e9;
  border-radius: 18rpx;
}

.account-cancel-limit b {
  color: #b42318;
  font-size: 26rpx;
}

.account-cancel-limit span {
  margin-top: 0;
  color: #9a4636;
  font-size: 26rpx;
  line-height: 1.4;
}

.account-cancel-mask {
  position: fixed;
  inset: 0;
  z-index: 70;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: rgba(15, 23, 42, 0.42);
}

.account-cancel-panel {
  width: min(100vw, 390px);
  padding: 32rpx 28rpx calc(34rpx + env(safe-area-inset-bottom));
  background: #fffaf6;
  border-radius: 36rpx 36rpx 0 0;
  box-shadow: 0 -24rpx 56rpx rgba(15, 23, 42, 0.24);
}

.account-cancel-panel h3,
.account-cancel-panel p {
  margin: 0;
}

.account-cancel-panel h3 {
  color: #172033;
  font-size: 34rpx;
  font-weight: 900;
}

.account-cancel-panel p {
  margin-top: 12rpx;
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.45;
}

.account-cancel-panel .account-cancel-limit {
  margin-top: 20rpx;
}

.clear-confirm-actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 24rpx;
}

.clear-confirm-actions button,
.clear-confirm-actions :deep(uni-button) {
  min-height: 72rpx;
  color: #8c6a58 !important;
  background: #ffffff !important;
  border: 1rpx solid #ead8cd;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.clear-confirm-actions button.danger,
.clear-confirm-actions :deep(uni-button.danger) {
  color: #ffffff !important;
  background: #b42318 !important;
  border-color: #b42318;
}
</style>
