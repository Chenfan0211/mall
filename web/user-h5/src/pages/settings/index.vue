<template>
  <view class="page no-tab settings-page">
    <view class="section">
      <text class="title">设置</text>
      <text class="subtle">账号安全、提货人和注销确认。</text>
    </view>

    <view class="section">
      <view class="menu-row" @click="showUserToast('账号安全接口暂缺，当前只展示登录状态')">
        <view>
          <text>账号安全</text>
          <text>{{ state.authenticated ? '已登录' : '未登录' }} · {{ state.mobileAuthorized ? '已授权手机号' : '未授权手机号' }}</text>
        </view>
        <text>›</text>
      </view>
      <view class="menu-row" @click="openReceivers">
        <view>
          <text>提货人</text>
          <text>维护结算页默认提货人</text>
        </view>
        <text>›</text>
      </view>
      <view class="menu-row" @click="askDestroy">
        <view>
          <text>注销确认</text>
          <text>注销会二次确认，当前不直接提交接口</text>
        </view>
        <text>›</text>
      </view>
    </view>

    <button class="danger logout-btn" @click="askLogout">退出登录</button>

    <ConfirmDialog
      :visible="dialog.visible"
      :title="dialog.title"
      :content="dialog.content"
      :confirm-text="dialog.confirmText"
      danger
      @cancel="dialog.visible = false"
      @confirm="confirmDialog"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { reactive } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import UserToast from '@/components/UserToast.vue';
import { logoutUser, showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();
const dialog = reactive({
    visible: false,
    action: '',
    title: '',
    content: '',
    confirmText: '确认'
});

function openReceivers() {
    uni.navigateTo({ url: '/pages/receivers/index' });
}

function askLogout() {
    dialog.visible = true;
    dialog.action = 'logout';
    dialog.title = '退出登录';
    dialog.content = '退出后订单、售后、收藏等资产页需要重新授权登录。';
    dialog.confirmText = '退出';
}

function askDestroy() {
    dialog.visible = true;
    dialog.action = 'destroy';
    dialog.title = '注销账号';
    dialog.content = '注销属于高风险操作，当前第一版仅保留确认交互，不直接提交注销接口。';
    dialog.confirmText = '我已知晓';
}

function confirmDialog() {
    dialog.visible = false;
    if (dialog.action === 'logout') {
        logoutUser();
        uni.redirectTo({ url: '/pages/login/index' });
        return;
    }
    showUserToast('注销接口暂未开放，请联系运营处理', 'warn');
}
</script>

<style lang="scss" scoped>
.menu-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f4e2d8;
}

.menu-row:last-child {
  border-bottom: 0;
}

.menu-row view {
  min-width: 0;
  display: grid;
  gap: 8rpx;
}

.menu-row view text:first-child {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.menu-row view text:last-child {
  overflow: hidden;
  color: #8c6a58;
  font-size: 23rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.menu-row > text {
  color: #c2412d;
  font-size: 44rpx;
  line-height: 1;
}

.logout-btn {
  width: calc(100% - 40rpx);
  margin: 24rpx 20rpx 0;
}
</style>
