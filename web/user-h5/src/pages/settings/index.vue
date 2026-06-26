<template>
  <view class="page">
    <view class="section">
      <text class="title">设置</text>
      <text class="subtle">账号安全、提货人和注销确认。</text>
    </view>
    <view class="section">
      <view v-for="item in items" :key="item" class="row">
        <text>{{ item }}</text>
      </view>
    </view>
    <button class="danger">退出登录</button>
  </view>
</template>

<script setup lang="ts">
const items = ['账号安全', '提货人', '注销确认'];
</script>

<style lang="scss" scoped>
.row {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}

.danger {
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}
</style>
