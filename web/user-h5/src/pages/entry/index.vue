<template>
  <view class="page no-tab entry-page">
    <view class="entry-head">
      <text>选择城市和自提点</text>
      <text>首次进入先确定服务范围，当前城市只有一个可用自提点时也不自动进入首页。</text>
    </view>

    <view class="city-card">
      <text class="title">当前城市</text>
      <view class="chip-row">
        <text class="pill active">广州</text>
        <text class="pill">定位当前城市</text>
      </view>
    </view>

    <view class="station-list">
      <button
        v-for="item in state.stations"
        :key="item.id"
        class="entry-station"
        :class="{ active: item.id === state.station.id, disabled: item.status !== 1 }"
        @click="select(item)"
      >
        <view>
          <text>{{ item.name }}</text>
          <text>{{ item.address }}</text>
          <text>{{ item.distance || '附近' }} · {{ item.deliveryTime || item.businessHours }}</text>
          <text v-if="item.status !== 1">{{ item.abnormalReason }}</text>
        </view>
        <text>{{ item.id === state.station.id ? '当前' : item.status === 1 ? '选择' : '不可选' }}</text>
      </button>
    </view>

    <button class="primary enter-btn" @click="goHome">进入首页</button>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import UserToast from '@/components/UserToast.vue';
import { type StationState, switchCurrentStation, useUserState } from '@/stores/userState';

const state = useUserState();

function select(item: StationState) {
    switchCurrentStation(item);
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}
</script>

<style lang="scss" scoped>
.entry-page {
  padding: 0 20rpx 40rpx;
}

.entry-head {
  display: grid;
  gap: 12rpx;
  margin: 0 -20rpx;
  padding: 60rpx 28rpx 54rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34, #f28a42);
}

.entry-head text:first-child {
  font-size: 46rpx;
  font-weight: 900;
}

.entry-head text:last-child {
  color: rgba(255, 255, 255, 0.88);
  font-size: 24rpx;
  line-height: 1.5;
}

.city-card {
  margin-top: -28rpx;
  padding: 24rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(126, 76, 49, 0.08);
}

.station-list {
  display: grid;
  gap: 16rpx;
  margin-top: 18rpx;
}

.entry-station {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 84rpx;
  gap: 16rpx;
  justify-content: stretch;
  min-height: 142rpx;
  padding: 20rpx;
  color: #172033 !important;
  background: #ffffff !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
  text-align: left;
  box-shadow: 0 10rpx 24rpx rgba(126, 76, 49, 0.08);
}

.entry-station.active {
  border-color: #e85d3f;
  background: #fff1e9 !important;
}

.entry-station.disabled {
  opacity: 0.6;
}

.entry-station view text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.entry-station view text:first-child {
  font-size: 30rpx;
  font-weight: 900;
}

.entry-station view text:not(:first-child) {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.entry-station > text {
  align-self: center;
  color: #c2412d;
  font-size: 22rpx;
  font-weight: 900;
}

.enter-btn {
  width: 100%;
  margin-top: 24rpx;
}
</style>
