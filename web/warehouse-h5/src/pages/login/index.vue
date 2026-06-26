<template>
  <view class="page">
    <view class="section hero">
      <text class="title">仓配登录</text>
      <text class="subtle">登录后按角色加载当前仓库任务。</text>
    </view>
    <view class="section">
      <input v-model="form.username" class="input" placeholder="账号" />
      <input v-model="form.password" class="input" password placeholder="密码" />
      <picker :range="roles" @change="changeRole">
        <view class="picker">{{ form.role || '选择角色' }}</view>
      </picker>
      <button class="primary" @click="submit">登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { passwordLogin } from '@/api/auth';
import { setToken } from '@/utils/request';

const roles = ['收货员', '采购员', '拣货员', '装车员', '司机', '仓库主管'];
const form = reactive({
    username: '',
    password: '',
    role: roles[0]
});

function changeRole(event: { detail: { value: number | string } }) {
    form.role = roles[Number(event.detail.value)];
}

async function submit() {
    const result = await passwordLogin({
        username: form.username,
        password: form.password,
        portalCode: 'warehouse-h5',
        role: form.role
    });
    setToken(result.accessToken || result.token || '');
    uni.switchTab({ url: '/pages/workbench/index' });
}
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

.input,
.picker {
  height: 88rpx;
  margin-bottom: 20rpx;
  padding: 0 24rpx;
  background: #f8fbf7;
  border: 1rpx solid #d3e7df;
  border-radius: 24rpx;
  line-height: 88rpx;
}

.primary {
  color: #ffffff;
  background: #117b6d;
  border-radius: 24rpx;
}
</style>
