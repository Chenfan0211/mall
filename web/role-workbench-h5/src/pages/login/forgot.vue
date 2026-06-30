<template>
  <view class="page no-tab">
    <view class="role-hero">
      <view class="status-bar">
        <text>14:49</text>
        <text>忘记密码</text>
      </view>
      <text class="subtle">{{ profile.roleName }}</text>
      <text class="title">重置登录密码</text>
      <text class="subtle">验证码 5 分钟内有效，绑定手机号用于确认当前身份。</text>
    </view>

    <view class="role-login-card">
      <view class="role-login-title">
        <view>
          <text>忘记密码</text>
          <text>{{ profile.roleName }}</text>
        </view>
      </view>
      <view class="role-form">
        <view class="role-form-row">
          <text>账号</text>
          <input v-model="form.username" placeholder="请输入账号" />
        </view>
        <view class="role-form-row">
          <text>手机号</text>
          <input v-model="form.mobile" placeholder="请输入绑定手机号" />
        </view>
        <view class="role-form-row code-row">
          <text>验证码</text>
          <input v-model="form.code" placeholder="请输入验证码" />
          <button class="role-code-btn" @click="sendCode">获取验证码</button>
        </view>
        <view class="role-form-row">
          <text>新密码</text>
          <input v-model="form.password" password placeholder="请输入新密码" />
        </view>
        <view class="role-form-row">
          <text>确认</text>
          <input v-model="confirmPassword" password placeholder="再次输入新密码" />
        </view>
        <text class="role-form-tip">绑定手机号：{{ profile.boundMobile }}。请使用当前账号绑定手机号完成身份校验。</text>
      </view>
      <button class="role-login-btn" @click="submit">重置密码</button>
      <view class="role-login-meta">
        <button @click="backLogin">返回登录</button>
        <text>重置成功后返回登录页</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { resetPassword } from '@/api/auth';
import { currentProfile, currentRole, showRoleToast, type RoleType } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';

const profile = computed(() => currentProfile.value);
const defaultAccounts: Record<RoleType, string> = {
    station: 'test_station',
    supplier: 'test_supplier_h5'
};
const form = reactive({
    username: defaultAccounts[currentRole.value],
    mobile: profile.value.mobilePlain,
    code: '',
    password: ''
});
const confirmPassword = ref('');

function sendCode() {
    showRoleToast('请联系后台获取验证码');
}

async function submit() {
    if (!form.username || !form.mobile || !form.code || !form.password) {
        showRoleToast('请完整填写重置信息');
        return;
    }
    if (form.password !== confirmPassword.value) {
        showRoleToast('两次输入的新密码不一致');
        return;
    }
    try {
        await resetPassword({ ...form, roleType: currentRole.value });
        showRoleToast('密码已重置，请使用新密码登录');
        backLogin();
    } catch (error) {
        showRoleToast(friendlyErrorMessage(error, '重置密码失败'));
    }
}

function backLogin() {
    uni.redirectTo({ url: '/pages/login/index' });
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

.role-login-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 20rpx;
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

.role-form {
  display: grid;
  gap: 20rpx;
}

.role-form-row {
  display: grid;
  grid-template-columns: 112rpx minmax(0, 1fr);
  align-items: center;
  gap: 16rpx;
  min-height: 84rpx;
  padding: 0 24rpx;
  background: #fff9f5;
  border: 1rpx solid #f1dbcd;
  border-radius: 24rpx;
}

.role-form-row text {
  color: #8b6048;
  font-size: 24rpx;
  font-weight: 800;
}

.role-form-row input {
  min-height: 76rpx;
  padding: 0;
  background: transparent;
  border: 0;
  border-radius: 0;
}

.code-row {
  grid-template-columns: 112rpx minmax(0, 1fr) 184rpx;
}

.role-code-btn,
.role-code-btn uni-button {
  min-height: 60rpx;
  color: #d94b34;
  background: #ffffff;
  border: 1rpx solid #f0cdbb;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-form-tip {
  display: block;
  margin: 4rpx 4rpx 0;
  color: #8f6c58;
  font-size: 24rpx;
  line-height: 1.55;
}

.role-login-btn {
  width: 100%;
  min-height: 88rpx;
  margin-top: 24rpx;
  color: #ffffff;
  background: #e85d3f;
  border-radius: 999rpx;
  font-size: 30rpx;
  font-weight: 900;
  box-shadow: 0 20rpx 40rpx rgba(232, 93, 63, 0.22);
}

.role-login-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 18rpx;
  color: #9a6a4d;
  font-size: 24rpx;
}

.role-login-meta button,
.role-login-meta uni-button {
  min-height: auto;
  color: #c2412d;
  background: transparent;
  font-size: 24rpx;
  font-weight: 900;
}

@media screen and (max-width: 360px) {
  .role-form-row,
  .code-row {
    grid-template-columns: 96rpx minmax(0, 1fr);
  }

  .code-row .role-code-btn {
    grid-column: 2;
    width: 100%;
  }
}
</style>
