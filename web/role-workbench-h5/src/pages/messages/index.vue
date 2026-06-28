<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>消息中心</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">作业、审核和资金通知</text>
      <text class="subtle">只展示本人 / 本主体通知记录。</text>
    </view>

    <view class="action-row message-actions">
      <button class="plain" @click="readCurrent">当前分类已读</button>
      <button class="primary" @click="readAll">全部已读</button>
    </view>

    <view class="tabs">
      <button v-for="item in groups" :key="item" :class="{ active: activeGroup === item }" @click="activeGroup = item">
        {{ item }}
      </button>
    </view>

    <view class="card-list">
      <view v-for="item in rows" :key="item.title" class="message-card" :class="{ unread: item.readStatus !== 1 }" @click="markOne(item.title)">
        <view class="card-head">
          <view class="card-main">
            <text class="card-title">{{ item.title }}</text>
            <text class="card-desc">{{ item.group }} · {{ item.time }}</text>
          </view>
          <text class="status-pill" :class="item.readStatus === 1 ? 'green' : 'orange'">{{ item.readStatus === 1 ? '已读' : '未读' }}</text>
        </view>
        <text class="message-copy">当前说明：{{ item.status }}</text>
      </view>
      <view v-if="rows.length === 0" class="empty-panel">
        <text class="title">暂无该类消息</text>
        <text class="subtle">后续通知会保留在这里。</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { currentProfile, markMessagesRead, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const groups = ['作业提醒', '审核结果', '异常反馈', '资金通知'];
const activeGroup = ref(groups[0]);
const rows = computed(() => profile.value.messages.filter((item) => item.group === activeGroup.value));

function readCurrent() {
    markMessagesRead(activeGroup.value);
    showRoleToast(`${activeGroup.value}已全部标记为已读`);
}

function readAll() {
    markMessagesRead();
    showRoleToast('全部消息已标记为已读');
}

function markOne(title: string) {
    const item = profile.value.messages.find((row) => row.title === title);
    if (item) {
        item.readStatus = 1;
        showRoleToast('消息已读');
    }
}
</script>

<style lang="scss" scoped>
.message-actions {
  margin-bottom: 16rpx;
}

.message-actions button {
  flex: 1;
  min-height: 64rpx;
  font-size: 24rpx;
}

.message-card.unread {
  border-color: #f2d6c4;
  background: #fff7f1;
}

.message-copy {
  display: block;
  margin-top: 16rpx;
  color: #8f6c58;
  font-size: 24rpx;
}

.tabs {
  overflow-x: auto;
}

.tabs button {
  min-width: 140rpx;
}
</style>
