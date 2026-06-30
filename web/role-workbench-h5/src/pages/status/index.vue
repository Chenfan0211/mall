<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ profile.roleName }}</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="backHome">‹</button>
        <view>
          <text class="role-detail-title">{{ stateCopy.title }}</text>
          <text class="role-detail-meta">{{ profile.entity }} · {{ stateCopy.desc }}</text>
        </view>
      </view>
    </view>

    <view class="role-main">
      <view class="role-state-card">
        <text class="role-state-title">{{ stateSummary.title }}</text>
        <text class="role-state-desc">{{ stateSummary.desc }}</text>
        <view class="role-state-grid">
          <view v-for="row in stateSummary.rows" :key="row.label">
            <text>{{ row.label }}</text>
            <text>{{ row.value }}</text>
          </view>
        </view>
      </view>
      <view class="role-empty status-desc">
        <text>{{ stateCopy.detail }}</text>
      </view>
      <view class="role-order-actions">
        <button class="role-filter-clear" @click="backHome">{{ stateCopy.action }}</button>
        <button class="role-filter-clear primary" @click="backHome">返回首页</button>
      </view>
    </view>
    <RoleBottomNav active="home" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { currentProfile, currentRole, type SubjectStatus } from '@/stores/role';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const status = ref<SubjectStatus>('loading');
const routeStatus = ref('');
const profile = computed(() => currentProfile.value);
const copies: Record<SubjectStatus, { title: string; desc: string; detail: string; action: string }> = {
    normal: {
        title: '身份正常',
        desc: '当前主体可以正常处理工作台任务。',
        detail: '系统会按当前角色和主体读取数据，不跨身份展示。',
        action: '继续处理工作台任务'
    },
    loading: {
        title: '数据加载中',
        desc: '正在加载当前身份、当前主体的数据。',
        detail: '加载期间会保留已选身份，不会切换到其他主体。',
        action: '等待接口返回'
    },
    loadFailed: {
        title: '数据加载失败',
        desc: '数据加载失败，请重试；当前身份信息已保留。',
        detail: '如果多次失败，请检查网络或联系后台确认账号权限。',
        action: '返回首页重试'
    },
    noIdentity: {
        title: '暂无可用身份',
        desc: '当前账号没有可用主体。',
        detail: '团点账号需要绑定自提点或团长身份，供应商账号需要绑定供应商主体。',
        action: '联系后台绑定主体'
    },
    subjectDisabled: {
        title: '主体已停用',
        desc: '当前主体已停用或合作终止。',
        detail: '停用主体只允许查看历史记录，不允许新增提交、提现、请假或到仓。',
        action: '查看历史记录'
    },
    noPermission: {
        title: '当前身份不可查看',
        desc: '历史链接或跨身份页面不可查看。',
        detail: '请返回当前身份首页，从工作台入口进入对应页面。',
        action: '返回工作台入口'
    }
};

const stateCopy = computed(() => copies[status.value] || copies.loading);
const stateSummary = computed(() => ({
    title: currentRole.value === 'supplier' ? '当前供应商状态' : '当前角色状态',
    desc: stateCopy.value.desc,
    rows: [
        { label: '当前角色', value: profile.value.roleName },
        { label: '主体', value: profile.value.entity },
        { label: '账号', value: profile.value.account || '-' },
        { label: '操作', value: stateCopy.value.action }
    ]
}));

onLoad((query) => {
    routeStatus.value = String(query?.status || 'loading');
    applyRouteStatus();
});

onShow(() => {
    syncRouteStatus();
});

onMounted(() => {
    syncRouteStatus();
    if (typeof window !== 'undefined') {
        window.addEventListener('hashchange', syncRouteStatus);
        window.addEventListener('popstate', syncRouteStatus);
    }
});

onUnmounted(() => {
    if (typeof window !== 'undefined') {
        window.removeEventListener('hashchange', syncRouteStatus);
        window.removeEventListener('popstate', syncRouteStatus);
    }
});

function applyRouteStatus() {
    const nextStatus = routeStatus.value as SubjectStatus;
    status.value = copies[nextStatus] ? nextStatus : 'loading';
}

function syncRouteStatus() {
    routeStatus.value = readCurrentStatus();
    applyRouteStatus();
}

function readCurrentStatus() {
    if (typeof location !== 'undefined') {
        const query = location.hash.split('?')[1] || '';
        const fromHash = new URLSearchParams(query).get('status');
        if (fromHash) return fromHash;
    }
    if (typeof getCurrentPages === 'function') {
        const pages = getCurrentPages();
        const current = pages[pages.length - 1] as unknown as { options?: { status?: string }; $page?: { options?: { status?: string } } };
        const fromPage = current?.options?.status || current?.$page?.options?.status;
        if (fromPage) return String(fromPage);
    }
    return routeStatus.value || 'loading';
}

function backHome() {
    uni.redirectTo({ url: '/pages/workbench/index' });
}
</script>

<style lang="scss" scoped>
.role-detail-headline {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: center;
  margin-top: 18rpx;
}

.role-detail-back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  min-height: 64rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.24);
  border-radius: 999rpx;
  font-size: 44rpx;
  font-weight: 900;
}

.role-detail-title {
  display: block;
  color: #ffffff;
  font-size: 38rpx;
  font-weight: 900;
  line-height: 1.25;
}

.role-detail-meta {
  display: block;
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-main {
  padding-top: 0;
}

.role-state-card {
  margin-bottom: 24rpx;
  padding: 24rpx 26rpx;
  color: #8b6b57;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.role-state-title {
  display: block;
  color: #2d241f;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.3;
}

.role-state-desc {
  display: block;
  margin-top: 8rpx;
  color: #8b6b57;
  font-size: 24rpx;
  line-height: 1.55;
}

.role-state-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 20rpx;
}

.role-state-grid view {
  min-width: 0;
  padding: 16rpx;
  background: #ffffff;
  border: 1rpx solid #f0d8ca;
  border-radius: 24rpx;
}

.role-state-grid text:first-child {
  display: block;
  color: #8b6a57;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-state-grid text:last-child {
  display: block;
  margin-top: 6rpx;
  overflow: hidden;
  color: #d94b34;
  font-size: 24rpx;
  font-weight: 900;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-desc {
  margin: 0 0 24rpx;
  padding: 34rpx 28rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.55;
  text-align: center;
}

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  width: 100%;
  justify-content: center;
}

.role-filter-clear,
uni-button.role-filter-clear {
  min-height: 66rpx;
  padding: 0 24rpx;
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-filter-clear.primary,
uni-button.role-filter-clear.primary {
  color: #ffffff;
  background: #e85d3f;
  border-color: #e85d3f;
}
</style>
