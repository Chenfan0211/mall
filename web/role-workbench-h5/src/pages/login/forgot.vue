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

    <view class="section">
      <view class="form-row">
        <text>账号</text>
        <input v-model="form.username" placeholder="请输入账号" />
      </view>
      <view class="form-row">
        <text>手机号</text>
        <input v-model="form.mobile" placeholder="请输入绑定手机号" />
      </view>
      <view class="form-row code-row">
        <view>
          <text>验证码</text>
          <input v-model="form.code" placeholder="请输入验证码" />
        </view>
        <button class="plain" @click="sendCode">获取验证码</button>
      </view>
      <view class="form-row">
        <text>新密码</text>
        <input v-model="form.password" password placeholder="请输入新密码" />
      </view>
      <view class="form-row">
        <text>确认密码</text>
        <input v-model="confirmPassword" password placeholder="再次输入新密码" />
      </view>
      <text class="form-tip">当前绑定手机号：{{ profile.boundMobile }}。原型验收验证码固定为 246810。</text>
      <button class="primary" @click="submit">重置密码</button>
      <button class="soft back-btn" @click="backLogin">返回登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { resetPassword } from '@/api/auth';
import { currentProfile, currentRole, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const form = reactive({
    username: profile.value.account,
    mobile: profile.value.mobilePlain,
    code: '',
    password: ''
});
const confirmPassword = ref('');

function sendCode() {
    form.code = '246810';
    showRoleToast('验证码已发送，请查看绑定手机号');
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
    } catch (error) {
        showRoleToast('重置密码接口暂未接通，已保留页面交互');
    }
    backLogin();
}

function backLogin() {
    uni.navigateBack({ delta: 1 });
}
</script>

<style lang="scss" scoped>
.form-row {
  margin-bottom: 20rpx;
}

.form-row > text,
.code-row text {
  display: block;
  margin-bottom: 10rpx;
  color: #5f493d;
  font-size: 24rpx;
  font-weight: 800;
}

.code-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 180rpx;
  gap: 14rpx;
  align-items: end;
}

.form-tip {
  display: block;
  margin-bottom: 20rpx;
  color: #8f6c58;
  font-size: 23rpx;
  line-height: 1.6;
}

.back-btn {
  width: 100%;
  margin-top: 14rpx;
}
</style>
