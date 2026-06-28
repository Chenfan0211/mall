<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '供应商' : '团点' }}</text>
      </view>
      <view class="head-row">
        <view>
          <text class="subtle">{{ profile.title }}</text>
          <text class="title">{{ roleType === 'supplier' ? '供应商工作台' : '自提点工作台' }}</text>
          <text class="subtle">{{ roleType === 'supplier' ? '采购、到仓、货款按当前供应商展示' : '团长能力 + 门店作业 + 退货处理' }}</text>
        </view>
        <button class="ghost role-switch" @click="switchRole">{{ roleType === 'supplier' ? '切团点' : '切供应商' }}</button>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">{{ profile.entity }}</text>
          <text class="subtle">{{ profile.subtitle }}</text>
        </view>
        <text class="status-pill green">当前身份</text>
      </view>
      <view class="detail-row">
        <text>账号</text>
        <text>{{ profile.account }}</text>
      </view>
      <view class="detail-row">
        <text>手机号</text>
        <text>{{ profile.boundMobile }}</text>
      </view>
    </view>

    <view class="metric-grid">
      <view v-for="item in profile.kpis" :key="item.label" class="metric-card" @click="open(item.page)">
        <text class="value">{{ item.value }}</text>
        <text class="label">{{ item.label }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">我需要处理</text>
          <text class="subtle">{{ roleType === 'supplier' ? '采购 / 到仓' : '今日作业顺序' }}</text>
        </view>
      </view>
      <view class="task-list">
        <view v-for="item in profile.tasks" :key="item.title" class="task-card" @click="open(item.page)">
          <view class="task-main">
            <text class="task-title">{{ item.title }}</text>
            <text class="task-desc">{{ item.desc }}</text>
          </view>
          <text class="status-pill orange">{{ item.tag || '处理' }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">我已提交，等待处理</text>
          <text class="subtle">只看本人 / 本主体</text>
        </view>
      </view>
      <view class="task-list">
        <view v-for="item in waitingRows" :key="item.no" class="task-card" @click="open('/pages/records/index')">
          <view class="task-main">
            <text class="task-title">{{ item.no }} · {{ item.type }}</text>
            <text class="task-desc">{{ item.title }}；下一步：{{ item.next }}</text>
          </view>
          <text class="status-pill" :class="statusClass(item.status)">{{ item.status }}</text>
        </view>
        <view v-if="waitingRows.length === 0" class="empty-panel">
          <text class="title">暂无等待处理事项</text>
          <text class="subtle">提交休假、提现、异常、采购或到仓凭证后会显示在这里。</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">快捷入口</text>
          <text class="subtle">固定入口</text>
        </view>
      </view>
      <view class="quick-grid">
        <button v-for="item in profile.quicks" :key="item.title" class="quick-button" @click="open(item.page)">
          <text class="task-title">{{ item.title }}</text>
          <text class="task-desc">{{ item.desc }}</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { currentProfile, currentRole, goPage, setRoleType, statusClass } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const waitingRows = computed(() =>
    profile.value.operations.filter((item) => /等待|待审核|待处理|已提交/.test(item.status)).slice(0, 3)
);

function open(url: string) {
    goPage(url);
}

function switchRole() {
    setRoleType(roleType.value === 'supplier' ? 'station' : 'supplier');
    uni.showToast({ title: `已切换为${currentRole.value === 'supplier' ? '供应商' : '团点'}视角`, icon: 'none' });
}
</script>

<style lang="scss" scoped>
.head-row {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20rpx;
}

.role-switch {
  min-width: 150rpx;
  min-height: 64rpx;
  font-size: 24rpx;
}
</style>
