<template>
  <view class="page">
    <view class="section">
      <text class="title">角色任务</text>
      <text class="subtle">按当前仓库角色显示允许处理的动作。</text>
    </view>
    <view v-for="item in roles" :key="item.title" class="section row">
      <view>
        <text class="title">{{ item.title }}</text>
        <text class="subtle">{{ item.tip }}</text>
      </view>
      <text class="pill">{{ item.status }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
const roles = [
    { title: '收货员', tip: '扫码收货、质检、差异', status: '可处理' },
    { title: '采购员', tip: '采购确认、到仓追踪', status: '可处理' },
    { title: '拣货员', tip: '波次、拣货、缺货', status: '可处理' },
    { title: '司机', tip: '配送、签到、回仓', status: '可处理' }
];
</script>

<style lang="scss" scoped>
.row {
  display: flex;
  justify-content: space-between;
}
</style>
