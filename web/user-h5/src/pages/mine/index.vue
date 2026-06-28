<template>
  <view class="page mine-page">
    <view class="mine-hero">
      <view class="avatar">{{ state.authenticated ? state.user.avatarText : '未' }}</view>
      <view class="mine-info">
        <text class="mine-name">{{ state.authenticated ? state.user.nickname : '未登录' }}</text>
        <text class="mine-station">默认自提点：{{ state.station.name }}</text>
      </view>
      <text class="mine-badge" @click="loginOrSetting">{{ state.authenticated ? '普通会员' : '去登录' }}</text>
    </view>

    <view class="mine-order-card">
      <view class="card-title-row">
        <text class="title">我的服务</text>
        <text class="subtle">订单、售后、消息</text>
      </view>
      <view class="service-grid">
        <view v-for="item in menus" :key="item.text" class="service-item" @click="open(item.url)">
          <view class="service-icon">{{ item.icon }}</view>
          <text>{{ item.text }}</text>
          <text>{{ item.tip }}</text>
        </view>
      </view>
    </view>

    <view class="mine-card">
      <view class="menu-row" @click="open('/pages/favorite/index')">
        <view>
          <text>我的收藏</text>
          <text>正常/失效商品</text>
        </view>
        <text>›</text>
      </view>
      <view class="menu-row" @click="open('/pages/history/index')">
        <view>
          <text>浏览历史</text>
          <text>按日期查看</text>
        </view>
        <text>›</text>
      </view>
      <view class="menu-row" @click="open('/pages/notice/index')">
        <view>
          <text>商品通知</text>
          <text>到货提醒、售罄通知</text>
        </view>
        <text>›</text>
      </view>
      <view class="menu-row" @click="open('/pages/receivers/index')">
        <view>
          <text>提货人管理</text>
          <text>结算默认提货人</text>
        </view>
        <text>›</text>
      </view>
      <view class="menu-row" @click="open('/pages/reviews/mine')">
        <view>
          <text>我的评价</text>
          <text>已发表评价和低评分记录</text>
        </view>
        <text>›</text>
      </view>
      <view class="menu-row" @click="open('/pages/settings/index')">
        <view>
          <text>账号设置</text>
          <text>账号安全、提货人和注销确认</text>
        </view>
        <text>›</text>
      </view>
    </view>
    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { navigateUser, useUserState } from '@/stores/userState';

const state = useUserState();
const menus = [
    { text: '站内消息', tip: '未读提醒', icon: '信', url: '/pages/message/index' },
    { text: '我的订单', tip: '履约状态', icon: '单', url: '/pages/order/index' },
    { text: '售后记录', tip: '退款进度', icon: '退', url: '/pages/aftersale/index' },
    { text: '团长申请', tip: '审核记录', icon: '团', url: '/pages/station/apply' }
];

function open(url: string) {
    if (url === '/pages/order/index') {
        uni.switchTab({ url });
        return;
    }
    navigateUser(url);
}

function loginOrSetting() {
    if (state.authenticated) {
        navigateUser('/pages/settings/index');
        return;
    }
    navigateUser('/pages/login/index');
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  padding: 0 20rpx 150rpx;
  background:
    radial-gradient(circle at 82% 6%, rgba(255, 214, 172, 0.85), transparent 24%),
    linear-gradient(180deg, #d94b34 0%, #ef7a37 250rpx, #f7f1ea 252rpx, #f7f1ea 100%);
}

.mine-hero {
  display: grid;
  grid-template-columns: 104rpx minmax(0, 1fr) auto;
  gap: 18rpx;
  align-items: center;
  padding: 54rpx 4rpx 72rpx;
  color: #ffffff;
}

.avatar {
  display: grid;
  width: 104rpx;
  height: 104rpx;
  place-items: center;
  color: #d94b34;
  background: #ffffff;
  border-radius: 50%;
  font-size: 40rpx;
  font-weight: 900;
  box-shadow: 0 12rpx 28rpx rgba(121, 74, 43, 0.16);
}

.mine-info {
  min-width: 0;
}

.mine-name,
.mine-station {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mine-name {
  color: #ffffff;
  font-size: 38rpx;
  font-weight: 900;
}

.mine-station {
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.86);
  font-size: 24rpx;
}

.mine-badge {
  min-height: 50rpx;
  padding: 0 18rpx;
  color: #c2412d;
  background: #ffffff;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  line-height: 50rpx;
}

.mine-order-card,
.mine-card {
  margin-top: -38rpx;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(126, 76, 49, 0.08);
}

.mine-card {
  margin-top: 18rpx;
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12rpx;
}

.service-item {
  display: grid;
  justify-items: center;
  min-width: 0;
  text-align: center;
}

.service-icon {
  display: grid;
  width: 72rpx;
  height: 72rpx;
  margin-bottom: 10rpx;
  place-items: center;
  color: #d94b34;
  background: #fff1e9;
  border: 1rpx solid #f4d4ca;
  border-radius: 22rpx;
  font-size: 26rpx;
  font-weight: 900;
}

.service-item text:nth-child(2) {
  color: #172033;
  font-size: 23rpx;
  font-weight: 900;
}

.service-item text:nth-child(3) {
  margin-top: 4rpx;
  color: #8c6a58;
  font-size: 20rpx;
}

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
  display: grid;
  gap: 8rpx;
}

.menu-row view text:first-child {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.menu-row view text:last-child {
  color: #8c6a58;
  font-size: 23rpx;
}

.menu-row > text {
  color: #c2412d;
  font-size: 44rpx;
  line-height: 1;
}
</style>
