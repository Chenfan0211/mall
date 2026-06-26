<template>
  <view class="page">
    <view class="section">
      <text class="title">我的</text>
      <text class="subtle">账号：test_station · 角色：自提点</text>
    </view>
    <view class="section">
      <view v-for="item in items" :key="item" class="row">
        <text>{{ item }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const items = ['休假申请', '操作记录', '消息中心', '退出登录'];
</script>

<style lang="scss" scoped>
.row {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}
</style>
