<template>
  <view class="page">
    <view class="section hero">
      <text class="title">角色工作台登录</text>
      <text class="subtle">自提点、团长和供应商统一从这里进入。</text>
    </view>
    <view class="section">
      <input v-model="form.username" class="input" placeholder="账号" />
      <input v-model="form.password" class="input" password placeholder="密码" />
      <button class="primary" @click="submit">登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { passwordLogin } from '@/api/auth';
import { setToken } from '@/utils/request';

const form = reactive({
    username: '',
    password: ''
});

async function submit() {
    const result = await passwordLogin({ ...form, portalCode: 'role-workbench-h5' });
    setToken(result.accessToken || result.token || '');
    uni.switchTab({ url: '/pages/workbench/index' });
}
</script>

<style lang="scss" scoped>
.hero {
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
  border: 0;
  box-shadow: 0 16rpx 32rpx rgba(217, 75, 52, 0.22);
}

.hero .title,
.hero .subtle {
  color: #ffffff;
}

.input {
  height: 88rpx;
  margin-bottom: 20rpx;
  padding: 0 24rpx;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
}

.primary {
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}
</style>
