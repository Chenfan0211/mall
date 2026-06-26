<template>
  <view class="page">
    <view class="section result">
      <text class="title">支付成功</text>
      <text class="subtle">预计 06-27 到海珠测试自提点提货。</text>
    </view>
    <button class="primary" @click="goOrder">查看订单</button>
  </view>
</template>

<script setup lang="ts">
function goOrder() {
    uni.switchTab({ url: '/pages/order/index' });
}
</script>

<style lang="scss" scoped>
.result {
  text-align: center;
}

.primary {
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}
</style>
