<template>
  <view class="page roles-page">
    <view class="warehouse-header">
      <text class="subtle">角色任务</text>
      <text class="title">按当前仓库角色展示允许动作</text>
    </view>

    <view class="role-grid">
      <view v-for="item in roles" :key="item.title" class="warehouse-role-card">
        <view class="role-icon">{{ item.icon }}</view>
        <text class="role-title">{{ item.title }}</text>
        <text class="role-tip">{{ item.tip }}</text>
        <text class="pill">{{ item.status }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const roles = [
    { title: '收货员', tip: '扫码收货、质检、差异', status: '可处理', icon: '收' },
    { title: '采购员', tip: '采购确认、到仓追踪', status: '可处理', icon: '采' },
    { title: '拣货员', tip: '波次、拣货、缺货', status: '可处理', icon: '拣' },
    { title: '司机', tip: '配送、签到、回仓', status: '可处理', icon: '司' }
];
</script>

<style lang="scss" scoped>
.role-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.warehouse-role-card {
  min-height: 250rpx;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.97);
  border: 1rpx solid #d9e9e3;
  border-radius: 30rpx;
  box-shadow: 0 12rpx 40rpx rgba(17, 85, 76, 0.07);
}

.role-icon {
  display: grid;
  width: 76rpx;
  height: 76rpx;
  margin-bottom: 16rpx;
  place-items: center;
  color: #117b6d;
  background: #e7f6ef;
  border: 1rpx solid #cce8dc;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 900;
}

.role-title,
.role-tip {
  display: block;
}

.role-title {
  color: #173b3a;
  font-size: 28rpx;
  font-weight: 900;
}

.role-tip {
  min-height: 64rpx;
  margin: 8rpx 0 16rpx;
  color: #628078;
  font-size: 23rpx;
  line-height: 1.4;
}
</style>
