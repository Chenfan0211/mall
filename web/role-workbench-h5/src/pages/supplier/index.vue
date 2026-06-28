<template>
  <view class="page no-tab">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>供应商视角</text>
      </view>
      <text class="subtle">兼容入口</text>
      <text class="title">进入供应商工作台</text>
      <text class="subtle">后续主入口统一使用底部工作台，同一套页面按角色切换内容。</text>
    </view>

    <view class="section">
      <button class="primary" @click="enter">切换并进入供应商工作台</button>
      <button class="soft back-btn" @click="back">返回</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { setRoleType } from '@/stores/role';

function enter() {
    setRoleType('supplier');
    uni.switchTab({ url: '/pages/workbench/index' });
}

function back() {
    uni.navigateBack({ delta: 1 });
}
</script>

<style lang="scss" scoped>
.back-btn {
  width: 100%;
  margin-top: 18rpx;
}
</style>
