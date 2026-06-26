<template>
  <view class="page">
    <view class="section profile">
      <text class="title">测试用户正常</text>
      <text class="subtle">默认自提点：海珠测试自提点</text>
    </view>
    <view class="section menu">
      <view v-for="item in menus" :key="item.text" class="menu-row" @click="open(item.url)">
        <text>{{ item.text }}</text>
        <text class="subtle">{{ item.tip }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const menus = [
    { text: '我的收藏', tip: '正常/失效', url: '/pages/favorite/index' },
    { text: '浏览历史', tip: '按日期', url: '/pages/history/index' },
    { text: '站内消息', tip: '未读提醒', url: '/pages/message/index' },
    { text: '团长申请', tip: '审核记录', url: '/pages/station/apply' },
    { text: '设置', tip: '账号安全', url: '/pages/settings/index' }
];

function open(url: string) {
    uni.navigateTo({ url });
}
</script>

<style lang="scss" scoped>
.profile {
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
  border: 0;
  box-shadow: 0 16rpx 32rpx rgba(217, 75, 52, 0.22);
}

.profile .title,
.profile .subtle {
  color: #ffffff;
}

.menu-row {
  display: flex;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}
</style>
