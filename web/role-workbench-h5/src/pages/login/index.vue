<template>
  <view class="page no-tab">
    <view class="role-hero">
      <view class="status-bar">
        <text>14:49</text>
        <text>角色工作台</text>
      </view>
      <text class="subtle">{{ profile.roleName }}</text>
      <text class="title">登录进入工作台</text>
      <text class="subtle">{{ profile.loginNote }}</text>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">选择入口</text>
          <text class="subtle">同一账号按当前身份读取对应主体数据</text>
        </view>
      </view>
      <view class="login-role-switch">
        <button :class="{ active: roleType === 'station' }" @click="chooseRole('station')">
          <text>团点</text>
          <text>团长 / 自提点</text>
        </button>
        <button :class="{ active: roleType === 'supplier' }" @click="chooseRole('supplier')">
          <text>供应商</text>
          <text>采购 / 到仓 / 结算</text>
        </button>
      </view>
    </view>

    <view class="section">
      <view class="form-row">
        <text>账号</text>
        <input v-model="form.username" placeholder="请输入账号" />
      </view>
      <view class="form-row">
        <text>密码</text>
        <input v-model="form.password" password placeholder="请输入密码" />
      </view>
      <text class="form-tip">测试账号：{{ profile.account }}，默认密码：{{ profile.password }}</text>
      <button class="primary login-submit" :disabled="loading" @click="submit">
        {{ loading ? '登录中' : '登录进入工作台' }}
      </button>
      <view class="login-links">
        <button class="link-btn" @click="goForgot">忘记密码</button>
        <text>联系后台开通账号</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">身份状态预览</text>
          <text class="subtle">用于验收无身份、停用、无权限、加载失败等页面</text>
        </view>
      </view>
      <view class="state-grid">
        <button v-for="item in states" :key="item.type" class="soft" @click="openState(item.type)">
          {{ item.label }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { passwordLogin } from '@/api/auth';
import {
    currentProfile,
    currentRole,
    setRoleAccount,
    setRoleToken,
    setRoleType,
    showRoleToast,
    type RoleType,
    type SubjectStatus
} from '@/stores/role';
import { setToken } from '@/utils/request';

const roleType = computed(() => currentRole.value);
const profile = computed(() => currentProfile.value);
const loading = ref(false);
const form = reactive({
    username: profile.value.account,
    password: profile.value.password
});

const states: Array<{ type: SubjectStatus; label: string }> = [
    { type: 'loading', label: '加载中' },
    { type: 'loadFailed', label: '加载失败' },
    { type: 'noIdentity', label: '暂无身份' },
    { type: 'subjectDisabled', label: '主体停用' },
    { type: 'noPermission', label: '无权限' }
];

function chooseRole(nextRole: RoleType) {
    setRoleType(nextRole);
    form.username = currentProfile.value.account;
    form.password = currentProfile.value.password;
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
        const token = result.accessToken || result.token || 'local-role-token';
        setToken(token);
        setRoleToken(token);
        setRoleAccount(form.username);
        uni.switchTab({ url: '/pages/workbench/index' });
    } catch (error) {
        setToken('local-role-token');
        setRoleToken('local-role-token');
        setRoleAccount(form.username);
        showRoleToast('后端登录接口暂不可用，已进入前端验收数据');
        uni.switchTab({ url: '/pages/workbench/index' });
    } finally {
        loading.value = false;
    }
}

function goForgot() {
    uni.navigateTo({ url: '/pages/login/forgot' });
}

function openState(status: SubjectStatus) {
    uni.navigateTo({ url: `/pages/status/index?status=${status}` });
}
</script>

<style lang="scss" scoped>
.login-role-switch {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.login-role-switch button {
  display: grid;
  gap: 8rpx;
  min-height: 122rpx;
  padding: 22rpx;
  color: #7a4f34;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 26rpx;
  text-align: left;
}

.login-role-switch button.active {
  color: #ffffff;
  background: #2b241f;
  border-color: #2b241f;
}

.login-role-switch text:first-child {
  font-size: 30rpx;
  font-weight: 900;
}

.login-role-switch text:last-child {
  font-size: 23rpx;
}

.form-row {
  margin-bottom: 20rpx;
}

.form-row > text {
  display: block;
  margin-bottom: 10rpx;
  color: #5f493d;
  font-size: 24rpx;
  font-weight: 800;
}

.form-tip {
  display: block;
  margin-bottom: 20rpx;
  color: #8f6c58;
  font-size: 23rpx;
  line-height: 1.6;
}

.login-submit {
  width: 100%;
}

.login-links {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18rpx;
  color: #8f6c58;
  font-size: 23rpx;
}

.link-btn {
  min-height: auto;
  color: #c2412d;
  background: transparent;
  font-size: 24rpx;
  font-weight: 900;
}

.state-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}
</style>
