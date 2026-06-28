<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>我的</text>
      </view>
      <text class="subtle">{{ profile.roleName }}</text>
      <text class="title">{{ profile.entity }}</text>
      <text class="subtle">账号：{{ profile.account }} · 手机：{{ profile.boundMobile }}</text>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">身份信息</text>
          <text class="subtle">{{ roleType === 'supplier' ? '当前供应商主体' : '当前团长 / 自提点主体' }}</text>
        </view>
        <text class="status-pill green">正常</text>
      </view>
      <view class="detail-row">
        <text>主体编号</text>
        <text>{{ profile.subjectId }}</text>
      </view>
      <view class="detail-row">
        <text>数据范围</text>
        <text>{{ roleType === 'supplier' ? '仅当前供应商' : '仅当前自提点' }}</text>
      </view>
    </view>

    <view class="section">
      <view v-for="item in menu" :key="item.title" class="menu-row" @click="open(item.url)">
        <view>
          <text class="card-title">{{ item.title }}</text>
          <text class="card-desc">{{ item.desc }}</text>
        </view>
        <text>›</text>
      </view>
    </view>

    <view class="section">
      <button class="soft switch-btn" @click="switchRole">{{ roleType === 'supplier' ? '切换团点视角' : '切换供应商视角' }}</button>
      <button class="danger logout-btn" @click="logout">退出登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { confirmAction, currentProfile, currentRole, goPage, logoutRole, setRoleType, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const menu = computed(() => {
    const base = [
        { title: '消息中心', desc: '作业提醒、审核结果、资金通知', url: '/pages/messages/index' },
        { title: '操作记录', desc: '休假、提现、异常、通知、采购和到仓凭证', url: '/pages/records/index' },
        { title: '服务工具', desc: '按当前角色展示可用工具', url: '/pages/service/index' }
    ];
    if (roleType.value === 'station') {
        base.unshift({ title: '休假申请', desc: '休假记录、申请和撤销', url: '/pages/leave/index' });
    }
    return base;
});

function open(url: string) {
    goPage(url);
}

function switchRole() {
    setRoleType(roleType.value === 'supplier' ? 'station' : 'supplier');
    showRoleToast(`已切换为${currentRole.value === 'supplier' ? '供应商' : '团点'}视角`);
    uni.switchTab({ url: '/pages/workbench/index' });
}

async function logout() {
    const ok = await confirmAction('确认退出当前角色工作台账号？', '退出登录');
    if (!ok) return;
    logoutRole();
    uni.reLaunch({ url: '/pages/login/index' });
}
</script>

<style lang="scss" scoped>
.menu-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}

.menu-row:last-child {
  border-bottom: 0;
}

.menu-row > text {
  color: #c2412d;
  font-size: 40rpx;
}

.switch-btn,
.logout-btn {
  width: 100%;
  margin-top: 16rpx;
}

.switch-btn {
  margin-top: 0;
}
</style>
