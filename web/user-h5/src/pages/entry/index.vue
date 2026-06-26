<template>
  <view class="page">
    <view class="section hero">
      <text class="title">选择自提点</text>
      <text class="subtle">下单前需要确认城市和自提点。</text>
    </view>
    <view class="section">
      <view class="row">
        <text>当前城市</text>
        <text class="strong">广州</text>
      </view>
      <view class="row">
        <text>推荐自提点</text>
        <text class="strong">海珠测试自提点</text>
      </view>
      <button class="primary" @click="goHome">进入首页</button>
    </view>
  </view>
</template>

<script setup lang="ts">
function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}
</script>

<style lang="scss" scoped>
.hero {
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
}

.row {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}

.strong {
  font-weight: 700;
}

.primary {
  margin-top: 28rpx;
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}
</style>
