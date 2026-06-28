<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ profile.roleName }}</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ stateCopy.title }}</text>
      <text class="subtle">{{ stateCopy.desc }}</text>
    </view>

    <view class="section status-card">
      <text class="status-icon">{{ stateCopy.icon }}</text>
      <text class="title">{{ stateCopy.title }}</text>
      <text class="subtle">{{ stateCopy.detail }}</text>
      <view class="action-row">
        <button class="plain" @click="refresh">刷新当前身份</button>
        <button class="primary" @click="backHome">返回首页</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { currentProfile, showRoleToast, type SubjectStatus } from '@/stores/role';

const status = ref<SubjectStatus>('loading');
const profile = computed(() => currentProfile.value);
const copies: Record<SubjectStatus, { icon: string; title: string; desc: string; detail: string }> = {
    normal: {
        icon: '✓',
        title: '身份正常',
        desc: '当前主体可以正常处理工作台任务。',
        detail: '系统会按当前角色和主体读取数据，不跨身份展示。'
    },
    loading: {
        icon: '…',
        title: '数据加载中',
        desc: '正在加载当前身份、当前主体的数据。',
        detail: '加载期间会保留已选身份，不会切换到其他主体。'
    },
    loadFailed: {
        icon: '!',
        title: '数据加载失败',
        desc: '数据加载失败，请重试；当前身份信息已保留。',
        detail: '如果多次失败，请检查网络或联系后台确认账号权限。'
    },
    noIdentity: {
        icon: '?',
        title: '暂无可用身份',
        desc: '当前账号没有可用主体。',
        detail: '团点账号需要绑定自提点或团长身份，供应商账号需要绑定供应商主体。'
    },
    subjectDisabled: {
        icon: '×',
        title: '主体已停用',
        desc: '当前主体已停用或合作终止。',
        detail: '停用主体只允许查看历史记录，不允许新增提交、提现、请假或到仓。'
    },
    noPermission: {
        icon: '!',
        title: '当前身份不可查看',
        desc: '历史链接或跨身份页面不可查看。',
        detail: '请返回当前身份首页，从工作台入口进入对应页面。'
    }
};

const stateCopy = computed(() => copies[status.value] || copies.loading);

onLoad((query) => {
    const nextStatus = String(query?.status || 'loading') as SubjectStatus;
    status.value = copies[nextStatus] ? nextStatus : 'loading';
});

function refresh() {
    showRoleToast('已刷新当前身份状态');
}

function backHome() {
    uni.switchTab({ url: '/pages/workbench/index' });
}
</script>

<style lang="scss" scoped>
.status-card {
  display: grid;
  gap: 20rpx;
  justify-items: center;
  text-align: center;
}

.status-icon {
  display: grid;
  width: 120rpx;
  height: 120rpx;
  place-items: center;
  color: #d94b34;
  background: #fff2e9;
  border: 1rpx solid #f2d6c4;
  border-radius: 36rpx;
  font-size: 58rpx;
  font-weight: 900;
}

.action-row {
  width: 100%;
}

.action-row button {
  flex: 1;
}
</style>
