<template>
  <view class="page no-tab">
    <view class="role-hero">
      <view class="status-bar">
        <text>14:49</text>
        <text>角色工作台</text>
      </view>
      <view class="role-brand-row">
        <view class="role-brand">
          <text>角色工作台</text>
          <text>{{ roleType === 'supplier' ? '供应商协同入口' : '自提点协同入口' }}</text>
        </view>
        <text class="role-badge">{{ profile.roleName }}</text>
      </view>
    </view>

    <view class="role-login-card">
      <view class="role-login-title">
        <view>
          <text>登录</text>
          <text>{{ roleType === 'supplier' ? '先选供应商入口' : '先选团点入口' }}</text>
        </view>
      </view>

      <view class="role-switch">
        <button class="role-option" :class="{ active: roleType === 'station' }" @click="chooseRole('station')">
          <text>团点</text>
          <text>团长/自提点</text>
        </button>
        <button class="role-option" :class="{ active: roleType === 'supplier' }" @click="chooseRole('supplier')">
          <text>供应商</text>
          <text>供应商角色</text>
        </button>
      </view>

      <view class="role-form">
        <view class="form-row role-form-row">
          <text>账号</text>
          <input v-model="form.username" placeholder="请输入账号" />
        </view>
        <view class="form-row role-form-row">
          <text>密码</text>
          <input v-model="form.password" password placeholder="请输入密码" />
        </view>
      </view>

      <button class="role-login-btn" :disabled="loading" @click="submit">
        {{ loading ? '登录中' : '登录进入工作台' }}
      </button>
      <view class="role-login-meta">
        <button @click="goForgot">忘记密码</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { passwordLogin } from '@/api/auth';
import {
    applyLoginResult,
    currentProfile,
    currentRole,
    setRoleToken,
    setRoleType,
    showRoleToast,
    type RoleType
} from '@/stores/role';
import { friendlyErrorMessage, setToken } from '@/utils/request';

const roleType = computed(() => currentRole.value);
const profile = computed(() => currentProfile.value);
const loading = ref(false);
const defaultCredentials: Record<RoleType, { username: string; password: string }> = {
    station: { username: 'test_station', password: 'Test@123456' },
    supplier: { username: 'test_supplier_h5', password: 'Test@123456' }
};
const form = reactive({
    username: defaultCredentials[currentRole.value].username,
    password: defaultCredentials[currentRole.value].password
});

watch(roleType, () => {
    applyDefaultCredential(roleType.value);
});

function chooseRole(nextRole: RoleType) {
    setRoleType(nextRole);
    applyDefaultCredential(nextRole);
}

function applyDefaultCredential(nextRole: RoleType) {
    form.username = defaultCredentials[nextRole].username;
    form.password = defaultCredentials[nextRole].password;
}

async function submit() {
    if (!form.username.trim() || !form.password.trim()) {
        showRoleToast('请输入账号和密码');
        return;
    }
    loading.value = true;
    try {
        const result = await passwordLogin({
            username: form.username,
            password: form.password,
            portalCode: 'role-workbench-h5',
            roleType: currentRole.value
        });
        const token = result.accessToken || result.token;
        if (!token) {
            throw new Error('登录结果异常，请稍后重试');
        }
        setToken(token);
        setRoleToken(token);
        applyLoginResult(result, form.username);
        uni.reLaunch({ url: '/pages/workbench/index' });
    } catch (error) {
        showRoleToast(friendlyErrorMessage(error, '登录失败，请检查账号密码或稍后重试'));
    } finally {
        loading.value = false;
    }
}

function goForgot() {
    uni.navigateTo({ url: '/pages/login/forgot' });
}
</script>

<style lang="scss" scoped>
.role-login-card {
  margin-top: -24rpx;
  position: relative;
  z-index: 2;
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.role-brand-row {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 14rpx;
}

.role-brand {
  display: grid;
  gap: 6rpx;
  color: #ffffff;
  font-weight: 900;
  line-height: 1.1;
}

.role-brand text:first-child {
  font-size: 22rpx;
  opacity: 0.9;
}

.role-brand text:last-child {
  font-size: 44rpx;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  min-height: 52rpx;
  padding: 0 24rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-login-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 14rpx;
}

.role-login-title text:first-child {
  display: block;
  color: #2b241f;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1.2;
}

.role-login-title text:last-child {
  display: block;
  margin-top: 6rpx;
  color: #8f6c58;
  font-size: 24rpx;
}

.role-switch {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6rpx;
  height: 74rpx;
  margin-bottom: 18rpx;
  padding: 6rpx;
  background: #fff2e9;
  border-radius: 999rpx;
  box-shadow: inset 0 0 0 1rpx #f2d6c4;
  box-sizing: border-box;
}

.role-option,
.role-option uni-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 62rpx;
  min-height: 62rpx;
  margin: 0;
  padding: 0;
  color: #7a4f34;
  background: transparent;
  border: 0;
  border-radius: 999rpx;
  font-weight: 900;
  line-height: 1;
  overflow: hidden;
}

.role-option::after {
  border: 0;
}

.role-option.active,
.role-option uni-button.active {
  color: #ffffff;
  background: #e85d3f;
  box-shadow: 0 8rpx 18rpx rgba(232, 93, 63, 0.22);
}

.role-option text:first-child {
  display: inline-block;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1;
}

.role-option text:last-child {
  display: none;
}

.role-form {
  display: grid;
  gap: 16rpx;
}

.role-login-btn {
  margin-top: 22rpx;
  width: 100%;
  min-height: 82rpx;
  color: #ffffff;
  background: #e85d3f;
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 900;
  box-shadow: 0 20rpx 40rpx rgba(232, 93, 63, 0.22);
}

.role-login-btn[disabled] {
  opacity: 0.72;
}

.role-login-meta {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-top: 16rpx;
  color: #9a6a4d;
  font-size: 24rpx;
}

.role-login-meta button,
.role-login-meta uni-button {
  margin: 0;
  min-height: auto;
  color: #c2412d;
  background: transparent;
  font-size: 24rpx;
  font-weight: 900;
}
</style>
