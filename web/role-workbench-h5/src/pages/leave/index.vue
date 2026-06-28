<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>休假记录</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">休假申请</text>
      <text class="subtle">休假会影响选点和未支付订单继续支付，提交前需要二次确认。</text>
    </view>

    <view class="card-list">
      <view v-for="item in profile.leaveRequests" :key="item.no" class="record-card">
        <view class="card-head">
          <view class="card-main">
            <text class="card-title">{{ item.no }}</text>
            <text class="card-desc">{{ item.range }} · {{ item.days }} 天</text>
          </view>
          <text class="status-pill" :class="item.statusClass">{{ item.status }}</text>
        </view>
        <text class="leave-copy">原因：{{ item.reason }}；审核：{{ item.auditTime }}</text>
        <text v-if="item.rejectReason" class="leave-copy">拒绝原因：{{ item.rejectReason }}</text>
        <button v-if="item.status === '待审核'" class="soft cancel-btn" @click="cancel(item.no)">撤销申请</button>
      </view>
      <view v-if="profile.leaveRequests.length === 0" class="empty-panel">
        <text class="title">暂无休假记录</text>
        <text class="subtle">供应商视角暂不需要休假设置。</text>
      </view>
    </view>

    <view v-if="roleType === 'station'" class="fixed-bottom">
      <button class="primary" @click="apply">新增休假申请</button>
      <button class="plain" @click="backMine">返回我的</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { confirmAction, currentProfile, currentRole, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);

function apply() {
    uni.navigateTo({ url: '/pages/leave/apply' });
}

async function cancel(no: string) {
    const ok = await confirmAction(`确认撤销休假申请 ${no}？`, '撤销休假');
    if (ok) {
        showRoleToast('撤销休假接口暂未接通，已保留交互');
    }
}

function backMine() {
    uni.switchTab({ url: '/pages/mine/index' });
}
</script>

<style lang="scss" scoped>
.leave-copy {
  display: block;
  margin-top: 14rpx;
  color: #8f6c58;
  font-size: 24rpx;
  line-height: 1.6;
}

.cancel-btn {
  width: 100%;
  margin-top: 18rpx;
  min-height: 62rpx;
  font-size: 24rpx;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
