<template>
  <view class="page">
    <view class="section hero">
      <text class="title">我的</text>
      <text class="subtle">账号：test_wms_supervisor · 仓库：广州中心仓</text>
    </view>
    <view class="section">
      <view v-for="item in items" :key="item" class="row">
        <text>{{ item }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const items = ['权限说明', '操作记录', '退出登录'];
</script>

<style lang="scss" scoped>
.hero {
  color: #ffffff;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.06) 0 1rpx, transparent 1rpx 24rpx),
    linear-gradient(135deg, #073e3d 0%, #0f766b 55%, #2a9d7a 100%);
  border: 0;
  box-shadow: 0 24rpx 44rpx rgba(17, 85, 76, 0.14);
}

.hero .title,
.hero .subtle {
  color: #ffffff;
}

.row {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #d9e9e3;
}
</style>
